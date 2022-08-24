package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 响应对象
 * 该类的每一个实例用于表示一个响应,每个响应由三部分构成
 * 1:状态行
 * 2:响应头
 * 3:响应正文
 */
public class HttpServletResponse {
    //状态行相关信息
    private int statusCode = 200;
    private String statusReason = "ok";
    //响应头相关信息
    private Map<String,String> headers = new HashMap<>();

    //响应头相关信息


    //响应正文相关信息
    private File entity;//响应正文对应的实体文件
    private Socket socket;

    public HttpServletResponse(Socket socket){
        this.socket = socket;
    }
    public void response() throws IOException {
        //3.1发送状态行
        sendStatusLine();
        //3.2发送响应头
        sendHeaders();
        //3.3发送响应正文
        sendContent();
    }

    private void sendStatusLine() throws IOException {
        String line = "HTTP/1.1"+" "+statusCode+" "+statusReason;
        println(line);
    }

    private void sendHeaders() throws IOException {
        Set<Map.Entry<String,String>> entrySet =headers.entrySet();
        for(Map.Entry<String,String> e : entrySet){
            String key = e.getKey();
            String value = e.getValue();
            String line = key +": " +value;
            println(line);
        }
        //单独发送CRLF表示响应头部分发送完毕
        println("");
    }

    private void sendContent() throws IOException {
        OutputStream out = socket.getOutputStream();
        FileInputStream fis = new FileInputStream(entity);
        int len;
        byte[] buf = new byte[1024*10];
        while((len = fis.read(buf))!=-1){
            out.write(buf,0,len);
        }
        System.out.println("响应发送完毕");
    }


    /**
     * 发送响应头
     * @param line
     */
    private void println(String line) throws IOException {
        byte[] data = line.getBytes("ISO8859-1");
        OutputStream out = socket.getOutputStream();
        out.write(data);
        out.write(13);//发送一个回车符
        out.write(10);//发送一个换行符
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getEntity() {
        return entity;
    }

    public void setEntity(File entity) {
        String fileName = entity.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".")+1);
        String type = HttpContext.getMineType(ext);
        //添加两个响应头Content-Type 和Content-Length
        addHeader("Content-Type",type);
        addHeader("Content-Length",String.valueOf(entity.length()));
        this.entity = entity;
    }

    public void addHeader(String name,String value){
        this.headers.put(name,value);
    }
}
