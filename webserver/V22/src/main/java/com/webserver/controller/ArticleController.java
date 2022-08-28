package com.webserver.controller;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import com.webserver.vo.Article;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleController {
    private static File articleDir;

    static {
        articleDir = new File("./webserver/articles");
        if(!articleDir.exists()){
            articleDir.mkdirs();
        }
    }


    public void writeArticle(HttpServletRequest request, HttpServletResponse response){
        System.out.println("开始处理发表完章...");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");
        System.out.println(title+","+author+","+ content);

        if(title==null || author==null || content==null){
            File file = new File("./webserver/webapps/myweb/article_fail.html");
            response.setEntity(file);
            return;
        }
        Article article = new Article(title,author,content);
        File artileFile = new File(articleDir,title+".obj");
        if(!artileFile.exists()){
            try(
                    ObjectOutputStream oos = new ObjectOutputStream(
                            new FileOutputStream(
                                    artileFile
                            )
                    );
            ){
                oos.writeObject(article);
                System.out.println("写出完毕");
                File file = new File("./webserver/webapps/myweb/article_success.html");
                response.setEntity(file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else {
            File file = new File("./webserver/webapps/myweb/article_fail.html");
            response.setEntity(file);
            return;
        }





        System.out.println("发表文章处理完毕！");


    }

    public void showAllArticle(HttpServletRequest request,HttpServletResponse response){
        System.out.println("开始生成动态页面");
        List<Article> articlesList =new ArrayList<>();
        File[] artucles = articleDir.listFiles(f->f.getName().endsWith(".obj"));
        for(File artucleFile : artucles){
            try(
                    FileInputStream fis = new FileInputStream(artucleFile);
                    ObjectInputStream ois = new ObjectInputStream(fis)
                    ){
                Article article = (Article) ois.readObject();
                articlesList.add(article);

            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }

        articlesList.forEach(u-> System.out.println(u));

        try{
            PrintWriter pw = new PrintWriter("./articlesList.html","UTF-8");
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
            for(Article article : articlesList){
                pw.println("<tr>");
                pw.println("<td>"+article.getTitle()+"</td>");
                pw.println("<td>"+article.getAuthor()+"</td>");
                pw.println("</tr>");
            }
            pw.println("</table>");
            pw.println("</center>");
            pw.println("</body>");
            pw.println("</html>");
            pw.flush();

            File file  = new File("./articlesList.html");
            response.setEntity(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


        System.out.println("动态页面生成完毕");

    }
}
