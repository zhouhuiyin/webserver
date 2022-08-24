package com.webserver.http;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录所有HTTP协议相关的规定内容
 */
public class HttpContext {
     /*
        所有资源后缀与头信息Content-Type的值的对应关系
        key:资源后缀名
        value:Content-Type头对应的值
     */
    private static Map<String,String> mimeMapping = new HashMap<>();

    static {
        initMimeMapping();
    }

    private static void initMimeMapping(){
        mimeMapping.put("html","text/html");
        mimeMapping.put("css","text/css");
        mimeMapping.put("js","application/javascript");
        mimeMapping.put("png","image/png");
        mimeMapping.put("jpg","image/jpeg");
        mimeMapping.put("gif","image/gif");
    }
    /**
     * 根据资源后缀名获取头信息Content-Type的值
     * @param ext
     * @return
     */
    public static String getMineType(String ext){
        return mimeMapping.get(ext);
    }

    public static void main(String[] args) {
        String type = getMineType("png");
        System.out.println(type);
    }

}
