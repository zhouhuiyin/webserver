package com.webserver.controller;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import com.webserver.vo.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 处理用户相关的请求操作
 */
public class UserController {
    //该File对象表示用于表示所有用户信息保存的目录
    private static File userDir;

    static {
        userDir = new File("./webserver/users");
        if(!userDir.exists()){
            userDir.mkdirs();
        }
    }

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
        File userFile = new File(userDir,username+".obj");
        if(userFile.exists()){
            File file = new File("./webserver/webapps/myweb/reg_have_user.html");
            response.setEntity(file);
            return;
        }

        int age = Integer.parseInt(ageStr);
        //2将该用户信息保存
        //将当前用户以一个User对象形式序列化到文件中：用户名.obj
        User user = new User(username,password,nickname,age);
        try(
                ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(
                                userFile
                        )
                );
                ) {
            oos.writeObject(user);
            System.out.println("写出完毕！");

            File file = new File("./webserver/webapps/myweb/reg_success.html");
            response.setEntity(file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("注册处理完毕！");

    }
}
