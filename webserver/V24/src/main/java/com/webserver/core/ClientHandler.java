package com.webserver.core;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程任务
 * 负责与特定的客户端进行HTTP交互.
 */
public class ClientHandler implements Runnable{
    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1解析请求
            HttpServletRequest request = new HttpServletRequest(socket);
            HttpServletResponse response = new HttpServletResponse(socket);


            //2处理请求
            DispatcherServlet servlet = DispatcherServlet.getInstance();
            servlet.service(request,response);


            //3发送响应
            response.response();


        } catch (EmptyRequestException e) {
            //空请求不需要做任何处理,只是为了忽略上面try中后续处理
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                //响应后断开连接
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
