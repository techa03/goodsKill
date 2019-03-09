[![Codacy Badge](https://api.codacy.com/project/badge/Grade/112fce7b6cb54af0b2f8efaaf8da4c26)](https://app.codacy.com/app/275688448/goodsKill?utm_source=github.com&utm_medium=referral&utm_content=techa03/goodsKill&utm_campaign=Badge_Grade_Dashboard)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/techa03/goodsKill/pulls)
[![Build Status](https://travis-ci.org/techa03/goodsKill.svg?branch=dev_springboot_2.x)](https://travis-ci.org/techa03/goodsKill)
[![codecov](https://codecov.io/gh/techa03/goodsKill/branch/dev_springboot_2.x/graph/badge.svg)](https://codecov.io/gh/techa03/goodsKill)

# 前言
项目命名为goodsKill一方面有商品秒杀的意思(好像有点chinglish的味道)，另外也可理解为good skill，本项目就是希望搭建一套完整的项目框架，把一些好的技术和技巧整合进来，方便学习和查阅。

本项目为慕课网仿购物秒杀网站,系统分为用户注册登录、秒杀商品管理模块。注册登录功能目前使用shiro完成权限验证，前端页面基于bootstrap框架搭建，并使用bootstrap-validator插件进行表单验证。 此项目整体采用springMVC+RESTFUL风格，mybatis持久层框架，数据库密码采用AES加密保护（默认未开启）。采用dubbo+zookeeper实现服务分布式部署及调用。集成了支付宝支付功能（详见service模块），用户完成秒杀操作成功之后即可通过二维码扫码完成支付（本demo基于支付宝沙箱环境）。

本项目扩展了秒杀功能，集成了jmock完成service层的测试，支持数据库读写分离，同时项目使用travis持续集成，提交更新后即可触发travis自动构建并完成项目测试覆盖率报告。

## 分支介绍
`dev_gradle`分支为使用gradle构建工具管理项目依赖，`dev_maven`分支对应maven构建工具（目前为主分支，springframework版本4.x），`dev_springboot_2.x`分支基于最新springboot2.x构建简化配置（springframework版本5.x）。本分支集成了druid，swagger2以及pageHelper等功能，该项目功能目前比较简陋，功能还有很多不完善的地方，仅作学习参考之用，如果觉得本项目对你有帮助的请多多star支持一下~~~~。

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

#### 开发环境版本说明：
- JDK :JDK1.8+
- MYSQL :8.0+
- activemq: 5.8.0
- kafka: kafka_2.11-2.0.0
- mongoDb: 4.0+

其他环境版本暂未测试


#### 项目启动方法：

- 参照redis官网安装redis，本地默认端口启动activemq，kafka，zookeeper（zookeeper无需安装，dubbo已集成）;

- 找到seckill.sql,procedure.sql文件，在本地mysql数据库中建立seckill仓库并执行完成数据初始化操作;

- 二维码图片存放路径配置信息在goods-util中的seckill.properties文件中修改;

- 项目根目录goodsKill中执行mvn clean install

- 在service模块中找到GoodsKillRpcServiceApplication类main方法启动远程服务;

- 在web模块使用maven spring-boot插件运行spring-boot:run

- 编译好整个项目后使用tomcat发布server模块，端口号18080，上下文环境配置为goodsKill,部署成功后访问
http://localhost:18080/goodsKill/login，默认管理员账号admin123，密码：aa123456;

- 支付宝二维码接入指南：https://blog.csdn.net/techa/article/details/71003519

#### 并发场景：
目前实现了几种秒杀方案

测试地址：http://localhost:18080/goodsKill/swagger-ui.html#/
- 场景一：sychronized同步锁实现
- 场景二：redisson分布式锁实现
- 场景三：activemq实现
- 场景四：kafkamq实现
- 场景五：存储过程实现
- 场景六：实时等待秒杀处理结果
- 场景七：zookeeper分布式锁

#### 编译部署注意事项：
- 可通过http://localhost:18080/goodsKill/swagger-ui.html#/访问swagger主页


#### 错误排查
- 如果发现减少库存无效（update set number=number-1语句），请检查mysql是否为8.0+版本

#### 已知问题
- zookeeper分布式锁秒杀方案不能正常使用（maven分支中则不存在此问题，估计是依赖版本问题，修复中。。。）
- 存储过程秒杀方案不能正常使用（该部分可以参考maven分支，maven分支已修复，未同步到本分支，两个版本维护头疼啊ORZ）
- 强迫症会发现springboot控制台日志不是彩色的（已解决）

待补充。。。。
