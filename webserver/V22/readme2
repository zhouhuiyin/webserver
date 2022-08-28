完成显示所有文章列表的动态页面
流程:
1:在首页上点击超链接
2:在ArticleController中完成生成动态页面的操作
3:响应该动态页面

实现:
1:在首页webapps/myweb/index.html中添加超链接:文章列表
  对应的href为"/myweb/showAllArticle"
2:在ArticleController中添加方法:showAllArticle
  该方法将articles目录下的所有文章读取出来并生成动态页面
3:DispatcherServlet添加分支处理显示所有文章列表的请求

文章列表的页面中用一个表格展示文章信息，两列:第一列为标题，第二列为作者。无需将文章内容
展示出来。