package com.webserver.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

    static{
        initMimeMapping();
    }

    private static void initMimeMapping(){
        try {
            //创建一个Properties对象
            Properties properties = new Properties();
            //加载对应的properties文件
            properties.load(
                    HttpContext.class.getResourceAsStream("web.properties")
            );
            //将加载的文件中每组键值对遍历出来存入mimeMapping即可
            properties.forEach(
                    (k,v)-> mimeMapping.put(k.toString(),v.toString())
            );
            //初始化完毕后mimeMapping中应该有1011个元素


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据资源后缀名获取头信息Content-Type的值
     * @param ext
     * @return
     */
    public static String getMimeType(String ext){
        return mimeMapping.get(ext);
    }

    public static void main(String[] args) {
        String type = getMimeType("png");
        System.out.println(type);
    }
}
