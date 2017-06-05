本demo为仿购物秒杀网站,该系统分为用户注册登录、秒杀商品管理模块。 前端页面基于bootstrap框架搭建，并使用bootstrap-validator插件进行表单验证。 此项目整体采用springMVC+RESTFUL风格，持久层使用的是mybatis。使用maven模块化设计，并可根据环境加载不同的数据源配置文件，数据库密码采用AES加密保护。采用dubbo+zookeeper实现服务分布式部署及调用。集成了支付宝支付功能（详见service模块），用户完成秒杀操作成功之后即可通过二维码扫码完成支付（本demo基于支付宝沙箱环境）。
 
- 开发环境：JDK8,MySQL,intelljIDEA;
 
- JAVA应用服务器：tomcat8.0;
 
- 技术选型：SpringMVC,mybatis,Redis,Jsp,ajax,bootstrap,git,maven,mybatis-generator自动生成mapper插件;
 
- 代码质量控制：sonarQube


![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%88%AA%E5%9B%BE20170315174408.png)
 
数据库设计
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%B1%8F%E6%9C%AA%E5%91%BD%E5%90%8D.png)
