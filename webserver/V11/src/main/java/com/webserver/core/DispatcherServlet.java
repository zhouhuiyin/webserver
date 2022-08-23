package com.webserver.core;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;

/**
 * 该类用于完成请求的处理工作
 */
public class DispatcherServlet {
    /*
       单例模式  java23种设计模式之一
       当一个类仅需要一个实例时,可使用此模式.
       三个步骤:
       1:提供静态的私有的当前类的属性
       2:私有化构造方法
       3:提供静态的公开的获取当前类实例的方法
         在方法中可判断步骤1的静态属性是否为null,是,则实例化对象(仅此一次)
         否则直接返回该实例
    */
    private static DispatcherServlet obj;

    private DispatcherServlet(){

    }

    public static DispatcherServlet getInstance(){
        if(obj==null){
            obj = new DispatcherServlet();
        }
        return obj;
    }
    public void service(HttpServletRequest request, HttpServletResponse response){
        String path = request.getUri();
        //将./webapps/myweb/index.html响应给浏览器D:\code\webserver\webserver\webapps\myweb\index.html
        File file = new File("./webserver/webapps"+path);
        if(file.isFile()){//如果定位是文件
            response.setEntity(file);
            //添加两个响应头Content-Type 和Content-Length
            response.addHeader("Content-Type","text/html");
            response.addHeader("Content-Length",String.valueOf(file.length()));
        }else {//不是文件（要么不存在，要么是目录，404情况）
            file = new File("./webserver/webapps/root/404.html");
            response.setStatusCode(404);
            response.setStatusReason("NotFound");
            response.setEntity(file);
            response.addHeader("Content-Type","text/html");
            response.addHeader("Content-Length",String.valueOf(file.length()));
        }

        response.addHeader("Server","WebServer");
    }
}

