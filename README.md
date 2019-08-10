[![Codacy Badge](https://api.codacy.com/project/badge/Grade/112fce7b6cb54af0b2f8efaaf8da4c26)](https://app.codacy.com/app/275688448/goodsKill?utm_source=github.com&utm_medium=referral&utm_content=techa03/goodsKill&utm_campaign=Badge_Grade_Dashboard)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/techa03/goodsKill/pulls)
[![Build Status](https://travis-ci.org/techa03/goodsKill.svg?branch=master)](https://travis-ci.org/techa03/goodsKill)
[![codecov](https://codecov.io/gh/techa03/goodsKill/branch/dev_springboot_2.x/graph/badge.svg)](https://codecov.io/gh/techa03/goodsKill)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=techa03_goodsKill&metric=alert_status)](https://sonarcloud.io/dashboard?id=techa03_goodsKill)

# 前言
项目命名为goodsKill一方面有商品秒杀的意思(好像有点chinglish的味道)，另外也可理解为good skill，本项目就是希望搭建一套完整的项目框架，把一些好的技术和技巧整合进来，方便学习和查阅。

本项目为慕课网仿购物秒杀网站,系统分为用户注册登录、秒杀商品管理模块。注册登录功能目前使用shiro完成权限验证，前端页面基于bootstrap框架搭建，并使用bootstrap-validator插件进行表单验证。 此项目整体采用springMVC+RESTFUL风格，mybatis持久层框架，数据库密码采用AES加密保护（默认未开启）。采用dubbo+zookeeper实现服务分布式部署及调用。集成了支付宝支付功能（详见service模块），用户完成秒杀操作成功之后即可通过二维码扫码完成支付（本demo基于支付宝沙箱环境）。

本项目扩展了秒杀功能，集成了jmock完成service层的测试，支持数据库读写分离，同时项目使用travis持续集成，提交更新后即可触发travis自动构建并完成项目测试覆盖率报告。

## 分支介绍
`dev_gradle`分支为使用gradle构建工具管理项目依赖，`dev_maven`分支对应maven构建工具（springframework版本4.x），`dev_springboot_2.x`分支基于最新springboot2.x构建简化配置（springframework版本5.x）。该项目功能目前比较简陋，功能还有很多不完善的地方，仅作学习参考之用，如果觉得本项目对你有帮助的请多多star支持一下~~~~。

## 技术选型

### 后端技术:
技术 | 名称 | 官网
----|------|----
SpringBoot |  框架 | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)
MyBatis | ORM框架  | [http://www.mybatis.org/mybatis-3/zh/index.html](http://www.mybatis.org/mybatis-3/zh/index.html)
MyBatis Generator | 代码生成  | [http://www.mybatis.org/generator/index.html](http://www.mybatis.org/generator/index.html)
PageHelper | MyBatis物理分页插件  | [http://git.oschina.net/free/Mybatis_PageHelper](http://git.oschina.net/free/Mybatis_PageHelper)
Druid | 数据库监控连接池  | [https://github.com/alibaba/druid](https://github.com/alibaba/druid)
ZooKeeper | 分布式协调服务  | [http://zookeeper.apache.org/](http://zookeeper.apache.org/)
Dubbo | 分布式服务框架  | [http://dubbo.io/](http://dubbo.io/)
Redis | 分布式缓存数据库  | [https://redis.io/](https://redis.io/)
ActiveMQ | 消息队列  | [http://activemq.apache.org/](http://activemq.apache.org/)
KafkaMQ | 消息队列  | [http://kafka.apache.org/](http://kafka.apache.org/)
Logback | 日志组件  | [https://logback.qos.ch/](https://logback.qos.ch/)
Protobuf & json | 数据序列化  | [https://github.com/google/protobuf](https://github.com/google/protobuf)
Jenkins | 持续集成工具  | [https://jenkins.io/index.html](https://jenkins.io/index.html)
Maven | 项目构建管理  | [http://maven.apache.org/](http://maven.apache.org/)
SonarQube | 项目代码质量监控 | [https://www.sonarqube.org/](https://www.sonarqube.org/)
Swagger2 | 项目API文档生成及测试工具 | [http://swagger.io/](http://swagger.io/)
Jmock | mock类生成测试工具 | [http://www.jmock.org/](http://www.jmock.org/)
Jacoco | 测试覆盖率报告插件 | [http://www.eclemma.org/jacoco/](http://www.eclemma.org/jacoco/)
Shiro | 用户权限安全管理框架 | [https://shiro.apache.org/](https://shiro.apache.org/)
MongoDb | Mongo数据库 | [https://www.mongodb.com/](https://www.mongodb.com/)
MySql | MySQL数据库 | [https://www.mysql.com/](https://www.mysql.com/)
Reactor | 响应式开发 | [https://projectreactor.io/](https://projectreactor.io/)
Spring Session | Spring会话管理 | [https://spring.io/projects/spring-session](https://spring.io/projects/spring-session)
ElasticSearch | 全文搜索引擎 | [https://www.elastic.co](https://www.elastic.co)

### 前端技术:
技术 | 名称 | 官网
----|------|----
jQuery | 函式库  | [http://jquery.com/](http://jquery.com/)
Bootstrap | 前端框架  | [http://getbootstrap.com/](http://getbootstrap.com/)
LayUI | 前端UI框架 | [http://www.layui.com/](http://www.layui.com/)

### API接口
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20170623222039.png)

### API接口说明
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20180819224521.png)

### 页面展示

#### 登录：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701215746.png)

#### 注册：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701215804.png)

#### 主页：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701215849.png)

#### 用户角色权限管理：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20180727155310.png)
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20180727155353.png)
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20180727155400.png)
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20180727155408.png)

#### 支付宝二维码扫码支付：
![image](https://github.com/techa03/learngit/blob/techa03-patch-1/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701221505.png)

#### 项目模块介绍
```
goodsKill
|--goodskill-chat-provider                  ||聊天室服务提供者（待完成）
|--goodsKill-common                         ||项目公共服务（待补充）
|--goodsKill-es-provider                    ||elasticsearch搜索服务提供者
|   |--goodskill-es-api                     
|   |--goodskill-es-dao                     
|   |--goodskill-es-service                 
|--goodsKill-mongo-provider                 ||mongo存储服务提供者
|   |--goodskill-mongo-service              
|--goodsKill-spring-boot-provider           ||订单、用户、登录、商品管理服务提供者（待拆分）
|   |--goodsKill-api                        
|   |--goodsKill-dao                        
|   |--goodsKill-entry                      
|   |--goodsKill-generator                  
|   |--goodsKill-service                    
|   |--goodsKill-util                       
|--goodskill-spring-boot-starter            ||项目配置自动装配（待完成）
|--goodsKill-spring-boot-web-consumer       ||提供页面客户端访问，controller层在这一模块
|   |--goodsKill-web    
```

#### 开发环境版本说明：
- JDK :JDK1.8+
- MYSQL :8.0+
- activemq: 5.8.0
- kafka: kafka_2.11-2.0.0
- mongoDb: 4.0+
- elasticsearch: 6.4.3

其他环境版本暂未测试


#### 项目启动方法：

- 参照官网安装redis/mongoDb/activeMQ/kafkaMQ/zookeeper/mysql8.0+本地默认端口启动，其中mongoDB和kafka非必须;

- 找到seckill.sql,procedure.sql文件，在本地mysql数据库中建立seckill仓库并执行完成数据初始化操作;

- 二维码图片存放路径配置信息在application.yml中的QRCODE_IMAGE_DIR配置中修改;

- 数据库密码需要根据个人密码设置进行更改，数据库密码保存在application.yml，可以使用AESUtil工具类进行数据库密码加密替换master.password和slave.password（主从数据库信息可以一致）

- applicatio.yml已包含所有环境配置信息，根据个人需要切换环境配置修改，修改active属性值即可
 ```
 spring:
   profiles:
     active: dev
 ```

- 项目根目录goodsKill中执行mvn clean install

- 在service模块中找到GoodsKillRpcServiceApplication类main方法启动远程服务，不想安装mongoDB/kafkaMQ的同学，可以使用GoodsKillRpcServiceSimpleApplication类启动（只需安装redis/mysql/activeMQ/zookeeper并启动）;

- 在web模块使用maven spring-boot插件运行spring-boot:run

- 启动完成后访问http://localhost:18080/goodsKill/login登录页面，默认管理员账号admin123，密码：aa123456;

- 支付宝二维码接入指南：https://blog.csdn.net/techa/article/details/71003519

- 如已安装mongoDB，可以main方法启动MongoReactiveApplication，通过使用该服务操作mongo库

#### 打包部署方法
1. 进入goodsKill项目根目录
 ```
 mvn clean package -DskipTests=true -Pdev
 ```
2. 启动服务提供方服务
 ```
 cd 项目根目录/goodsKill-spring-boot-provider/goodsKill-service/target
 java -jar goodsKill-service.jar
 ```
3. 启动服务消费方（包含web容器）
- target目录找到goodsKill.war，使用tomcat 18080端口启动


#### 并发方案：
目前实现了几种秒杀方案

测试地址：http://localhost:18080/goodsKill/swagger-ui.html#/
- 场景一：sychronized同步锁实现
- 场景二：redisson分布式锁实现
- 场景三：activemq实现
- 场景四：kafkamq实现
- 场景五：存储过程实现
- 场景六：实时等待秒杀处理结果
- 场景七：zookeeper分布式锁
- 场景八：使用redis进行秒杀商品减库存操作，秒杀结束后异步发送MQ，使用mongoDb完成数据落地

可在web控台查看秒杀结果，打印信息类似：
 ```
2019-03-25 13:40:42.123  INFO 1016 --- [io-18080-exec-9] o.s.w.controller.SeckillMockController   : 秒杀活动开始，秒杀场景三(activemq消息队列实现)时间：Mon Mar 25 13:40:42 CST 2019,秒杀id：1001
2019-03-25 13:40:49.050  INFO 1016 --- [ jmsContainer-1] o.s.web.mqlistener.SeckillTopicListener  : 最终成功交易笔数：100
2019-03-25 13:40:49.050  INFO 1016 --- [ jmsContainer-1] o.s.web.mqlistener.SeckillTopicListener  : 秒杀活动结束，秒杀场景三(activemq消息队列实现)时间：Mon Mar 25 13:40:49 CST 2019,秒杀id：1001
 ```

#### 备忘：
- swagger主页：http://localhost:18080/goodsKill/swagger-ui.html#/

#### 后续更新计划
- 使用redis优化秒杀执行过程，异步发送秒杀成功MQ消息完成数据落地，并使用mongo存储秒杀信息提升TPS;(已完成，见场景八)
- 集成spring-session管理会话，目前使用shiro由于只保存在单机上，重启应用或分布式环境可能需要重新登录；(已完成)
- 添加秒杀用户聊天室功能，使用netty网络通信，maven分支已经实现，功能比较简单，后续需要完善；
- 模拟秒杀控台日志显示优化，后续考虑增加一个benchmark跑分功能，依次调用各个秒杀场景方案，最后输出各个方案的用时；
