上个版本中在DispatcherServlet的service方法中每次处理请求时为了设置资源对应
的响应头Content-Type,我们先实例化一个Map保存类型与头信息的对应关系.但实际上
这个Map没有必要每次都创建一遍,全局一份即可,每次仅需要根据资源后缀提取对应的响应
头的值即可.

实现:
1:在com.webserver.http包下新建类:HttpContext
  这个类用于维护所有HTTP协议规定的内容以便复用.
2:在HttpContext中定义一个静态属性:Map mimeMapping
3:在静态块中对该属性进行初始化
4:提供一个静态方法可根据资源后缀从这个Map中提取Content-Type对应的值
  该方法名为:getMimeType()
5:设置响应头Content-Type的值时就可以根据资源后缀调用步骤4提供的方法

6:将原DispatcherServlet中设置响应头Content-Type和Content-Length的
  工作移动到HttpServletResponse的设置响应正文方法setEntity中.
  原因:一个响应中只要包含正文就应当包含说明正文信息的两个头Content-Type和
  Content-Length.因此我们完全可以在设置正文的时候自动设置这两个头.
  这样做的好处是将来设置完正文(调用setEntity)后无需再单独设置这两个头了.