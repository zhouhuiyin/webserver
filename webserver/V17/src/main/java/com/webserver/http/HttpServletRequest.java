package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 该类的每一个实例表示客户端发送过来的一个请求.
 * 每个请求由三部分构成:
 * 1:请求行
 * 2:消息头
 * 3:消息正文
 */
public class HttpServletRequest {
    private Socket socket;
    //请求行相关信息
    private String method;//请求方式
    private String uri;//抽象路径
    private String protocol;//协议版本
    private String requestURI;//记录uri中"?"左侧的请求部分
    private String queryString;//记录uri中"?"右侧的参数部分
    private Map<String,String> parameters = new HashMap<>();//记录每一组参数

    //消息相关信息
    private Map<String,String> headers = new HashMap<>();

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //1.解析请求
        parseRequestLine();
        //2解析消息头
        parseHeaders();
        //3解析正文
        parseContent();

    }

    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();
        if(line.isEmpty()){//若请求的请求是空字符串说明本次为空请求
            throw new EmptyRequestException();
        }
        System.out.println("请求行："+line);
        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        parseUri();//进一步解析uri
        protocol = data[2];
        System.out.println("method:"+method);
        System.out.println("uri:"+uri);
        System.out.println("protocol:"+protocol);
    }

    private void parseUri(){
                /*
            uri有两种情况:
            1:含参数的
              例如:
                  |请求部分 |?|                   参数部分                          |
              uri:/myweb/reg?username=zhangsan&password=123456&nickname=asan&age=22
              将uri按照"?"拆分为请求部分与参数部分,将请求部分赋值给requestURI
              将参数部分赋值给queryString
              再将参数部分进行拆分,首先按照"&"拆分出每一组参数,每组参数再按照
              "="拆分为参数名与参数值,并将参数名作为key,参数值作为value保存到
              parameters这个Map中
            2:不含参数的
              例如:
              uri:/root/404.png
              直接将uri的值赋值给requestURI即可
         */
        String[] data = uri.split("\\?");
        requestURI = data[0];
        if(data.length>1){
            queryString = data[1];
            //拆分出每一组参数
            //[username=zhangsan, password=123456, ...]
            String[] paraArr = queryString.split("&");
            for(String para: paraArr){
                String[] paras = para.split("=");
                if(paras.length>1){
                    parameters.put(paras[0],paras[1]);
                }else {
                    parameters.put(paras[0],null);
                }
            }
        }

    }
    private void parseHeaders() throws IOException {
        while (true){
            String line = readLine();
            if(line.isEmpty()){//如果读取的是空字符串说明单独读取了CRLF
                break;
            }
            //Connection: keep-alive
            System.out.println("消息头："+line);
            String[] data = line.split(":\\s");
            headers.put(data[0],data[1] );
        }
        System.out.println("headers:"+ headers);
    }

    private void parseContent(){}
    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        StringBuilder builder = new StringBuilder();
        char cur = 'a';//保存本次读取到的字符
        char pre = 'a';//保存上次读取到的字符
        int d;
        while((d = in.read()) !=-1){
            cur = (char)d;//本次读取到的字符
            if(pre==13 && cur==10){//判断是否连续读取到了回车+换行
                break;
            }
            builder.append(cur);
            pre = cur;//下次循环前将本次读取到的字符记为上次读取的字符
        }
        return builder.toString().trim();
    }


    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String name){
        return headers.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String name){
        return parameters.get(name);
    }
}
