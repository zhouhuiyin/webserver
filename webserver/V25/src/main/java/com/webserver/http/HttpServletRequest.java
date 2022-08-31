package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
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

    //消息头相关信息
    private Map<String,String> headers = new HashMap<>();

    //消息正文相关信息
    private byte[] contentData;

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;
        //1解析请求行
        parseRequestLine();
        //2解析消息头
        parseHeaders();
        //3解析消息正文
        parseContent();
    }

    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();
        if(line.isEmpty()){//若请求的请求行是空字符串说明说本次为空请求
            throw new EmptyRequestException();
        }
        System.out.println("请求行:"+line);
        String[] data = line.split("\\s");
        method = data[0];
        uri = data[1];
        parseUri();//进一步解析uri
        protocol = data[2];
        System.out.println("method:"+method);
        System.out.println("uri:"+uri);
        System.out.println("protocol:"+protocol);
    }

    /**
     * 进一步解析uri
     */
    private void parseUri(){
        String[] data = uri.split("\\?");
        requestURI = data[0];
        if(data.length>1){//含有参数
            queryString = data[1];
            //拆分出每一组参数
            parseParameters(queryString);
        }
        System.out.println("requestURI:"+requestURI);
        System.out.println("queryString:"+queryString);
        System.out.println("parameters:"+parameters);
    }

    /**
     * 拆分参数初始化parameters这个Map
     * @param line 参数格式应当是:参数名1=参数值1&参数名2=参数值2&...
     */
    private void parseParameters(String line){
        String[] paraArr = line.split("&");
        for(String para : paraArr){//遍历数组拆分每一个参数的参数名和参数值
            //[username, zhangsan]
            String[] paras = para.split("=");
            if(paras.length>1) {
                parameters.put(paras[0], paras[1]);
            }else{
                parameters.put(paras[0], null);
            }
        }
    }


    private void parseHeaders() throws IOException {
        while(true) {
            String line = readLine();
            if(line.isEmpty()){//如果读取的是空字符串说明单独读取了CRLF
                break;
            }
            //Connection: keep-alive
            System.out.println("消息头:" + line);
            String[] data = line.split(":\\s");
            headers.put(data[0],data[1]);
        }
        System.out.println("headers:"+headers);
    }
    private void parseContent() throws IOException {
        //获取消息头Content-Length
        System.out.println(headers.containsKey("Content-Length"));
        if(headers.containsKey("Content-Length")){//判断是否有该头信息
            //获取正文长度
            int len = Integer.parseInt(headers.get("Content-Length"));
            contentData = new byte[len];
            //读取正文数据
            InputStream in = socket.getInputStream();
            in.read(contentData);//一次性块读所有正文数据

            //根据消息头Content-Type确定正文类型以便分支处理
            System.out.println(headers.containsKey("Content-Type"));
            if(headers.containsKey("Content-Type")){
                String contentType = headers.get("Content-Type");
                //分支判断不同类型进行不同处理
                if("application/x-www-form-urlencoded".equals(contentType)){
                    //当前正文数据就是原GET请求提交表单时抽象路径中"?"右侧的字符串(参数部分)
                    String line = new String(contentData,"ISO8859-1");
                    System.out.println("正文内容:"+line);
                    parseParameters(line);
                }
            }
        }



    }

    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();
        StringBuilder builder = new StringBuilder();
        char cur = 'a';//保存本次读取到的字符
        char pre = 'a';//保存上次读取到的字符
        int d;
        while((d = in.read())!=-1){
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

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    /**
     * 根据参数名获取参数值
     * @param name
     * @return
     */
    public String getParameter(String name){
        String value = parameters.get(name);
        if(value!=null){
            try {
                value = URLDecoder.decode(value,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
