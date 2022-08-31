package com.webserver.core;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/*
用于维护所有业务请求与对应的业务处理类
供DispatcherServlet处理请求时使用
 */
public class HandlerMapping {
    /**
     * 保存请求路径与对应业务处理
     * key：请求路径，如："/myweb/reg"
     * value:对应的业务处理方法
     */
    private static Map<String,MethodMapping> requestMapping = new HashMap<>();

    static {
        try {
            initRequestMapping();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private static void initRequestMapping() throws URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        File dir = new File(
                HandlerMapping.class.getClassLoader().getResource("./com/webserver/controller").toURI()
        );
        File[] files = dir.listFiles(f->f.getName().endsWith(".class"));
        for(File file : files){
            String fileName = file.getName().substring(0,file.getName().indexOf("."));
            Class cls = Class.forName("com.webserver.controller."+ fileName);
            //该类是否被@Controller标注
            if(cls.isAnnotationPresent(Controller.class)){
                Object controller = cls.newInstance();
                Method[] methods = cls.getDeclaredMethods();
                for(Method method : methods){
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping rm = method.getAnnotation(RequestMapping.class);
                        String path = rm.value();
                        MethodMapping mm = new MethodMapping(controller,method);
                        requestMapping.put(path,mm);
                    }
                }
            }
        }
        System.out.println("=========>"+requestMapping.size());
        System.out.println("=========>"+requestMapping);
    }

    /**
     * 根据请求路径获取某个Controller下使用注解@RequestMapping标注的处理该请求的方法
     * @param path
     * @return
     */
    public static MethodMapping getRequestMapping(String path){
        return requestMapping.get(path);

    }

    public static class MethodMapping{
        //Method是处理某请求的业务方法，Object为该方法所属对象
        //以便反射机制调用时可直接使用 method.invoke(controller,...)
        private Object controller;
        private Method method;

        public MethodMapping(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }

}
