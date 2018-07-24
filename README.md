[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/techa03/goodsKill/pulls)
[![Build Status](https://travis-ci.org/techa03/goodsKill.svg?branch=dev_maven)](https://travis-ci.org/techa03/goodsKill)

[![GitHub stars](https://img.shields.io/github/stars/techa03/goodsKill.svg?style=social&label=Stars)](https://github.com/techa03/goodsKill)
[![GitHub forks](https://img.shields.io/github/forks/techa03/goodsKill.svg?style=social&label=Fork)](https://github.com/techa03/goodsKill)
# 前言
本demo为慕课网仿购物秒杀网站,该系统分为用户注册登录、秒杀商品管理模块。 注册登录功能目前使用shiro完成权限验证，前端页面基于bootstrap框架搭建，并使用bootstrap-validator插件进行表单验证。 此项目整体采用springMVC+RESTFUL风格，mybatis持久层框架，数据库密码采用AES加密保护（默认未开启）。采用dubbo+zookeeper实现服务分布式部署及调用。集成了支付宝支付功能（详见service模块），用户完成秒杀操作成功之后即可通过二维码扫码完成支付（本demo基于支付宝沙箱环境）。

本项目扩展了秒杀功能，集成了jmock完成service层的测试，同时项目使用travis持续集成，提交更新后即可触发travis自动构建并完成项目测试覆盖率报告。

## 分支介绍
本项目目前主要有两个分支，`dev_gradle`分支为使用gradle构建工具管理项目依赖，`dev_maven`分支对应maven构建工具（目前为主分支），`dev_maven`部署方法见底部。本分支集成了druid，swagger2以及pageHelper等功能，该项目功能目前比较简陋，功能还有很多不完善的地方，仅作学习参考之用，如果觉得本项目对你有帮助的请多多star支持一下~~~~。

## 技术选型

### 后端技术:
技术 | 名称 | 官网
----|------|----
Spring Framework | 容器  | [http://projects.spring.io/spring-framework/](http://projects.spring.io/spring-framework/)
SpringMVC | MVC框架  | [http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#mvc](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#mvc)
MyBatis | ORM框架  | [http://www.mybatis.org/mybatis-3/zh/index.html](http://www.mybatis.org/mybatis-3/zh/index.html)
MyBatis Generator | 代码生成  | [http://www.mybatis.org/generator/index.html](http://www.mybatis.org/generator/index.html)
PageHelper | MyBatis物理分页插件  | [http://git.oschina.net/free/Mybatis_PageHelper](http://git.oschina.net/free/Mybatis_PageHelper)
Druid | 数据库监控连接池  | [https://github.com/alibaba/druid](https://github.com/alibaba/druid)
ZooKeeper | 分布式协调服务  | [http://zookeeper.apache.org/](http://zookeeper.apache.org/)
Dubbo | 分布式服务框架  | [http://dubbo.io/](http://dubbo.io/)
Redis | 分布式缓存数据库  | [https://redis.io/](https://redis.io/)
ActiveMQ | 消息队列  | [http://activemq.apache.org/](http://activemq.apache.org/)
Logback | 日志组件  | [https://logback.qos.ch/](https://logback.qos.ch/)
Protobuf & json | 数据序列化  | [https://github.com/google/protobuf](https://github.com/google/protobuf)
Jenkins | 持续集成工具  | [https://jenkins.io/index.html](https://jenkins.io/index.html)
Maven | 项目构建管理  | [http://maven.apache.org/](http://maven.apache.org/)
Gradle | 项目构建工具 | [https://gradle.org/](https://gradle.org/)
SonarQube | 项目代码质量监控 | [https://www.sonarqube.org/](https://www.sonarqube.org/)
Swagger2 | 项目API文档生成及测试工具 | [http://swagger.io/](http://swagger.io/)
Jmock | mock类生成测试工具 | [http://www.jmock.org/](http://www.jmock.org/)
Jacoco | 测试覆盖率报告插件 | [http://www.eclemma.org/jacoco/](http://www.eclemma.org/jacoco/)
Shiro | 用户权限安全管理框架 | [https://shiro.apache.org/](https://shiro.apache.org/)
### 前端技术:
技术 | 名称 | 官网
----|------|----
jQuery | 函式库  | [http://jquery.com/](http://jquery.com/)
Bootstrap | 前端框架  | [http://getbootstrap.com/](http://getbootstrap.com/)
LayUI | 前端UI框架 | [http://www.layui.com/](http://www.layui.com/)
### API接口
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20170623222039.png)

### 页面展示

#### 登录：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701215746.png)
#### 注册：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701215804.png)
#### 主页：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701215849.png)
#### 支付宝二维码扫码支付：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701221505.png)

#### 项目启动方法：

1.参照redis官网安装redis，本地默认端口启动activemq，zookeeper（zookeeper这个一定要装啊，不然启动不了，开启服务后倾检查2181端口是否正常开启了）;

2.找到seckill.sql文件，在本地mysql数据库中建立seckill仓库并执行seckill.sql完成数据初始化操作;

3.到service下的resources/profile/local/connections.properties根据需要修改数据库以及zookeeper等配置信息;

4.二维码图片存放路径配置信息在goods-util中的seckill.properties文件中修改;

5.在service模块中找到GoodsKillRpcServiceApplication类main方法启动远程服务;

6.编译好整个项目后使用tomcat发布server模块，端口号18080，上下文环境配置为goodsKill,部署成功后访问
http://localhost:18080/goodsKill/login，默认管理员账号admin123，密码：aa123456;

7.service和server下的resources/profile用于存放不同环境的配置信息，默认使用local目录的properties配置，如需应用其他环境下的配置文件，[请到此处修改配置](https://github.com/techa03/goodsKill/blob/dev_maven/pom.xml#L26-L54);

8.根据不同环境打包项目在根目录中使用maven命令:
```
mvn clean install -P<你的profileId>
eg:
mvn clean install -Plocal
```
9.支付宝二维码接入指南：https://blog.csdn.net/techa/article/details/71003519

#### 编译部署注意事项：

1.注意logback.xml和seckill.properties的文件路径配置信息，修改成你自定义的目录即可；

2.可通过http://localhost:18080/goodsKill/swagger-ui.html#/访问swagger主页
