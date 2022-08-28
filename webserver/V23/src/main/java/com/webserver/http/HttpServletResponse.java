package com.webserver.http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
    //用来响应动态数据作为正文使用
    private byte[] contentData;//响应正文对应的一组字节
    /**
     * java.io.ByteArrayOutputStream
     * 是一个低级流，其内部维护一个字节数组，通过这个流写出的数据全部会保存到
     * 内部的这个字节数组中。
     */
    private ByteArrayOutputStream  baos;

    private Socket socket;

    public HttpServletResponse(Socket socket){
        this.socket = socket;
    }
    public void response() throws IOException {
        sendBefore();
        //3.1发送状态行
        sendStatusLine();
        //3.2发送响应头
        sendHeaders();
        //3.3发送响应正文
        sendContent();
    }

    /**
     * 发送响应前的准备工作
     */
    private void sendBefore(){
        //1若baos不为null，说明向该输出流中写出了一组动态数据要作为正文，因此，要将该流中的这组字节获取到作为正文
        if(baos!=null){
            contentData = baos.toByteArray();
            //自动添加响应头Content—Length
            addHeader("Content-Length",String.valueOf(contentData.length));
        }


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
        if(contentData!=null){
            out.write(contentData);
        }else if (entity != null) {
            try (
                    FileInputStream fis = new FileInputStream(entity);
            ) {
                int len;
                byte[] buf = new byte[1024 * 10];
                while ((len = fis.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
            }

        }

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

    /**
     * 通过返回的PrintWriter写出的文本数据会作为响应正文发送给客户端
     * @return
     */
    public PrintWriter getWriter(){
       return new PrintWriter(
               new BufferedWriter(
                       new OutputStreamWriter(
                               getOutputStream(), StandardCharsets.UTF_8
                       )
               ),true
       );
    }

    /**
     * 通过返回的字节输出流写出的字节会被作为响应正文发送给客户端
     * @return
     */
    public OutputStream getOutputStream(){
        if(baos==null){
            baos = new ByteArrayOutputStream ();
        }
        return baos;
    }

    public void setContentType(String type){
        addHeader("Content-Type",type);
    }
}

