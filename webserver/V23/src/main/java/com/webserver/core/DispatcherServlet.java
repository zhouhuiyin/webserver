package com.webserver.core;

import com.webserver.controller.ArticleController;
import com.webserver.controller.UserController;
import com.webserver.http.HttpContext;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
        String path = request.getRequestURI();

        if("/myweb/reg".equals(path)){
            //处理用户注册
            UserController controller = new UserController();
            controller.reg(request,response);
        }else if("/myweb/login".equals(path)){
            UserController controller = new UserController();
            controller.login(request,response);
        }else if("/myweb/showAllUser".equals(path)){
            UserController controller = new UserController();
            controller.showAllUser(request, response);
        }else if("/myweb/writeArticle".equals(path)){
            ArticleController controller = new ArticleController();
            controller.writeArticle(request,response);
        }else if("/myweb/showAllArticle".equals(path)){
            ArticleController controller = new ArticleController();
            controller.showAllArticle(request,response);
        }else {
            //将./webapps/myweb/index.html响应给浏览器
            File file = new File("./webserver/webapps" + path);
            if (file.isFile()) {//如果定位是文件
                response.setEntity(file);
            } else {//不是文件(要么不存在,要么是目录,404情况)
                file = new File("./webserver/webapps/root/404.html");
                response.setStatusCode(404);
                response.setStatusReason("NotFound");
                response.setEntity(file);
            }
        }
        response.addHeader("Server","WebServer");

    }
}
