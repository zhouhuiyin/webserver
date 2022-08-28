package com.webserver.controller;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import com.webserver.vo.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
    /**
     * 处理用户登陆操作
     */
    public void login(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始处理登录");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username+","+password);
        if(username==null || password==null){
            File file = new File("./webserver/webapps/myweb/login_error.html");
            response.setEntity(file);
            return;
        }

        //登录
        File userFile = new File(userDir,username+".obj");
        if(userFile.exists()){
            //反序列化读取该用户信息
            try(
                    FileInputStream fis = new FileInputStream(userFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    ){
                User user = (User) ois.readObject();
                //比较密码
                if(user.getPassword().equals(password)){
                    //密码一致，登录成功
                    File file = new File("./webserver/webapps/myweb/login_success.html");
                    response.setEntity(file);
                    return;
                }
            }catch (IOException|ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        //处理登录失败
        File file = new File("./webserver/webapps/myweb/login_fail.html");
        response.setEntity(file);

        System.out.println("登录处理完毕!");

    }

    /**
     * 用于生成包含所有用户信息的动态页面
     * @param request
     */
    public void showAllUser(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始生成动态页面");
        //1读取users目录中的所有用户信息
        List<User> userList = new ArrayList<>();
        File[] subs = userDir.listFiles(f->f.getName().endsWith(".obj"));
        for(File userFile:subs){
            try(
                    FileInputStream fis = new FileInputStream(userFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    ){
                User user = (User)ois.readObject();
                userList.add(user);
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        userList.forEach(u-> System.out.println(u));
        //2将用户信息拼入一个html页面
        try{
            PrintWriter pw = new PrintWriter("./userList.html","UTF-8");
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>用户列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>用户列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr>");
            pw.println("<td>用户名</td>");
            pw.println("<td>密码</td>");
            pw.println("<td>昵称</td>");
            pw.println("<td>年龄</td>");
            pw.println("</tr>");
            for(User u : userList){
                pw.println("<tr>");
                pw.println("<td>"+u.getUsername()+"</td>");
                pw.println("<td>"+u.getPassword()+"</td>");
                pw.println("<td>"+u.getNickname()+"</td>");
                pw.println("<td>"+u.getAge()+"</td>");
                pw.println("</tr>");
            }
            pw.println("</table>");
            pw.println("</center>");
            pw.println("</body>");
            pw.println("</html>");
            pw.flush();
            File file = new File("./userList.html");
            response.setEntity(file);
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //3将页面作为正文响应给浏览器



        System.out.println("动态页面生成完毕");
    }
}
