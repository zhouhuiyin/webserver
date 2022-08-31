package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import qrcode.QRCodeUtil;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 工具
 * 用于生成二维码,验证码等使用
 */
@Controller
public class ToolsController {
    @RequestMapping("/myweb/createQR")
    public void createQr(HttpServletRequest request, HttpServletResponse response){
        try {
            String line = request.getParameter("content");
            System.out.println(line);

//            QRCodeUtil.encode(
//                    "http://doc.canglaoshi.org",
//                    "./qr.jpg"
//            );

            //            QRCodeUtil.encode(
//                    "http://doc.canglaoshi.org",
//                    fos);



            OutputStream out = response.getOutputStream();
            QRCodeUtil.encode(
                    line,//二维码内容
                    "./01.jpg",//中间的logo图片
                    out,//输出流,用于将图片写出到哪里去
                    true);//logo图片是否压缩(不压缩可能导致logo图把二维码盖住)

            response.setContentType("image/jpeg");

            System.out.println("生成完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



