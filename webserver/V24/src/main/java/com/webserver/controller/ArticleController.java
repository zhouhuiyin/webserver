package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import com.webserver.vo.Article;
import com.webserver.vo.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理与文章相关的业务
 */
@Controller
public class ArticleController {
    private static File articleDir;

    static {
        articleDir = new File("./webserver/articles");
        if (!articleDir.exists()) {
            articleDir.mkdirs();
        }
    }

    @RequestMapping("/myweb/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("处理发表文章");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");
        System.out.println(title + "," + author + "," + content);

        if (title == null || author == null || content == null) {
            File file = new File("./webapps/myweb/article_fail.html");
            response.setEntity(file);
            return;
        }

        File articleFile = new File(articleDir, title + ".obj");
        if (articleFile.exists()) {//要求标题不能重复
            File file = new File("./webapps/myweb/article_fail.html");
            response.setEntity(file);
            return;
        }

        Article article = new Article(title, author, content);
        try (
                FileOutputStream fos = new FileOutputStream(articleFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(article);
            File file = new File("./webapps/myweb/article_success.html");
            response.setEntity(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("发表文章处理完毕");
    }
    @RequestMapping("/myweb/showAllArticle")
    public void showAllArticle(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("生成文章列表");
        //1获取所有文章文件
        List<Article> articleList = new ArrayList<>();
        File[] subs = articleDir.listFiles(f -> f.getName().endsWith(".obj"));
        for (File articleFile : subs) {
            try (
                    FileInputStream fis = new FileInputStream(articleFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                Article article = (Article) ois.readObject();
                articleList.add(article);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        //2生成页面
        PrintWriter pw = response.getWriter();
        pw.println("<!DOCTYPE html>");
        pw.println("<html lang=\"en\">");
        pw.println("<head>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<title>文章列表</title>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<center>");
        pw.println("<h1>文章列表</h1>");
        pw.println("<table border=\"1\">");
        pw.println("<tr>");
        pw.println("<td>标题</td>");
        pw.println("<td>作者</td>");
        pw.println("</tr>");

        for (Article article : articleList) {
            pw.println("<tr>");
            pw.println("<td>" + article.getTitle() + "</td>");
            pw.println("<td>" + article.getAuthor() + "</td>");
            pw.println("</tr>");
        }
        pw.println("</table>");
        pw.println("</center>");
        pw.println("</body>");
        pw.println("</html>");

        System.out.println("写出完毕");

        response.setContentType("text/html");


        System.out.println("文章列表生成完毕");
    }
}
