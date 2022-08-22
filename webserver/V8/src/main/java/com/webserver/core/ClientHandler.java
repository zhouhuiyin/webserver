package com.webserver.core;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

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
            HttpServletResponse response = new HttpServletResponse(socket);
            //2.处理请求
            String path = request.getUri();
            //将./webapps/myweb/index.html响应给浏览器D:\code\webserver\webserver\webapps\myweb\index.html
            File file = new File("./webserver/webapps"+path);
            if(file.isFile()){//如果定位是文件
                response.setEntity(file);
            }else {//不是文件（要么不存在，要么是目录，404情况）
                file = new File("./webserver/webapps/root/404.html");
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setEntity(file);
            }
            //3.发送响应
            response.response();



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
