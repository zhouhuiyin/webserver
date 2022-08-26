package com.webserver.controller;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import jdk.nashorn.internal.codegen.CompilerConstants;

import java.io.File;

/**
 * 处理用户相关的请求操作
 */
public class UserController {
    /**
     * 处理用户注册操作的请求
     */
    public void reg(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理注册...");
        //1获取用户在注册页面上表单中提交上来的注册信息
        //调用getParameter用于获取浏览器传递过来的参数.
        //参数与表单中输入框的名字一致
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println(username+","+password+","+nickname+","+ageStr);
        /*
            必要的验证工作:
            1:要求上述四个信息用户必须在页面上输入,不能有null的
            2:年龄还必须是一个数字格式
            否则直接响应一个页面:reg_info_error.html
            该页面居中显示一行字:注册失败,注册信息输入有误,请重新注册.
         */
        if(username==null || password==null || nickname==null
        || ageStr==null || !ageStr.matches("[0-9]+")){
            File file = new File("./webserver/webapps/myweb/reg_info_error.html");
            response.setEntity(file);
            return;
        }

        /*
            用户名不能重复,若是重复用户名则直接响应页面:reg_have_user.html
            页面里居中显示一行字:该用户已存在,请重新注册.
         */




        System.out.println("注册处理完毕！");

    }
}
