package com.webserver.core;

import com.webserver.http.HttpServletRequest;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程任务
 * 负责与特定的客户端进行http交互
 */
public class ClientHandler implements Runnable{
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try {
            //http://localhost:8088
           //解析请求
            HttpServletRequest request = new HttpServletRequest(socket);
            //2.处理请求
            String path = request.getUri();
            //3.发送响应
            //将./webapps/myweb/index.html响应给浏览器D:\code\webserver\webserver\webapps\myweb\index.html
            File file = new File("./webserver/webapps"+path);
            OutputStream out = socket.getOutputStream();
            //3.1发送状态行
            String line = "HTTP/1.1 200 OK";
            byte[] data = line.getBytes("ISO8859-1");
            out.write(data);
            out.write(13);//发送一个回车符
            out.write(10);//发送一个换行符
            //3.2发送响应头
            line = "Content-Type: text/html";
            data = line.getBytes("ISO8859-1");
            out.write(data);
            out.write(13);//发送一个回车符
            out.write(10);//发送一个换行符

            line ="Content-Length: "+ file.length();
            data = line.getBytes("ISO8859-1");
            out.write(data);
            out.write(13);//发送一个回车符
            out.write(10);//发送一个换行符
            //单独发送CRLF表示响应头部分发送完毕
            out.write(13);
            out.write(10);
            //3.3发送响应正文
            FileInputStream fis = new FileInputStream(file);
            int len;
            byte[] buf = new byte[1024*10];
            while((len = fis.read(buf))!=-1){
                out.write(buf,0,len);
            }
            System.out.println("响应发送完毕");


        }catch (IOException e){
            e.printStackTrace();
        }finally {
            //响应后断开连接
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
