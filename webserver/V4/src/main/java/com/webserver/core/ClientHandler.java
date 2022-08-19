package com.webserver.core;

import java.io.IOException;
import java.io.InputStream;
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
            //1.解析请求
            String line = readLine();
            System.out.println("请求行："+line);
            //请求行相关信息
            String method;  //请求方式
            String uri;    //抽象路径
            String protocol;//协议版本
            String[] data = line.split("\\s");
            method = data[0];
            uri = data[1];
            protocol = data[2];
            System.out.println("method:"+method);
            System.out.println("uri:"+uri);
            System.out.println("protocol:"+protocol);
            //读取消息头
            Map<String,String> headers = new HashMap<>();
            while (true){
                line = readLine();
                if(line.isEmpty()){
                    break;
                }
                System.out.println("消息头："+line);
                data = line.split(":\\s");
                headers.put(data[0],data[1] );
            }
            System.out.println("headers:"+ headers);
            //2.处理请求
            //3.发送响应
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
}
