package com.webserver.vo;

import java.io.Serializable;

/**
 * VO:value object 值对象
 * 这种类是用来保存一组信息的.
 *
 * 比如我们保存一组与用户相关信息的数据时,就可以设计一个类User.用这个类的每一
 * 个实例表示一个具体用户的信息.而这个User类就是一个VO.
 */
public class User implements Serializable {
    public static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String nickname;
    private int age;

    public User(String username, String password, String nickname, int age) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", age=" + age +
                '}';
    }
}
