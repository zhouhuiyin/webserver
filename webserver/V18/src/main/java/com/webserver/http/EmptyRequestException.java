package com.webserver.http;
/**
 * 空请求异常
 * 当HttpServletRequest解析请求时发现这是一个空请求便会抛出该异常
 */
public class EmptyRequestException extends Exception{
    public EmptyRequestException(){

    }

    public EmptyRequestException(String message){
        super(message);
    }

    public EmptyRequestException(String message,Throwable cause){

    }

    public EmptyRequestException(Throwable cause){

    }

    public EmptyRequestException(String message,Throwable cause,boolean enableSuppression,boolean writableStackTrace){
        super(message,cause,enableSuppression,writableStackTrace);
    }
}
