[![Codacy Badge](https://api.codacy.com/project/badge/Grade/971f665ea1644854a500a7da0c93cba9)](https://app.codacy.com/app/275688448/goodsKill?utm_source=github.com&utm_medium=referral&utm_content=techa03/goodsKill&utm_campaign=Badge_Grade_Dashboard)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/techa03/goodsKill/pulls)
[![Build Status](https://travis-ci.org/techa03/goodsKill.svg?branch=dev_maven)](https://travis-ci.org/techa03/goodsKill)
[![release](https://img.shields.io/github/release/techa03/goodsKill.svg)](https://github.com/techa03/goodsKill/releases)
[![codecov](https://codecov.io/gh/techa03/goodsKill/branch/dev_maven/graph/badge.svg)](https://codecov.io/gh/techa03/goodsKill)

# 前言
项目命名为goodsKill一方面有商品秒杀的意思(好像有点chinglish的味道)，另外也可理解为good skill，本项目就是希望搭建一套完整的项目框架，把一些好的技术和技巧整合进来，方便学习和查阅。

本项目为慕课网仿购物秒杀网站,系统分为用户注册登录、秒杀商品管理模块。注册登录功能目前使用shiro完成权限验证，前端页面基于bootstrap框架搭建，并使用bootstrap-validator插件进行表单验证。 此项目整体采用springMVC+RESTFUL风格，mybatis持久层框架，数据库密码采用AES加密保护（默认未开启）。采用dubbo+zookeeper实现服务分布式部署及调用。集成了支付宝支付功能（详见service模块），用户完成秒杀操作成功之后即可通过二维码扫码完成支付（本demo基于支付宝沙箱环境）。

本项目扩展了秒杀功能，集成了jmock完成service层的测试，支持数据库读写分离，同时项目使用travis持续集成，提交更新后即可触发travis自动构建并完成项目测试覆盖率报告。

- 20190221 v2.5.0

秒杀列表页面新增聊天室功能，基于netty实现，支持在线用户实时聊天

## 分支介绍
`dev_gradle`分支为使用gradle构建工具管理项目依赖，`dev_maven`分支对应maven构建工具（目前为主分支，springframework版本4.x），`dev_springboot_2.x`分支基于最新springboot2.x构建简化配置（springframework版本5.x）。本分支集成了druid，swagger2以及pageHelper等功能，该项目功能目前比较简陋，功能还有很多不完善的地方，仅作学习参考之用，如果觉得本项目对你有帮助的请多多star支持一下~~~~。

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
KafkaMQ | 消息队列  | [http://kafka.apache.org/](http://kafka.apache.org/)
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
Netty | 异步事件驱动的网络应用程序框架 | [https://netty.io/](https://netty.io/)

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

#### 软件版本说明：
- JAVA版本:JDK1.8+;
- MYSQL版本:8.0+;


#### 项目启动方法：

- 参照redis官网安装redis，本地默认端口启动activemq，kafka，zookeeper（开启服务后倾检查2181端口是否正常开启了）;

- 找到seckill.sql,procedure.sql文件，在本地mysql数据库中建立seckill仓库并执行完成数据初始化操作;

- 到service下的resources/profile/local/connections.properties根据需要修改数据库以及zookeeper等配置信息;

- 二维码图片存放路径配置信息在goods-util中的seckill.properties文件中修改;

- 在service模块中找到GoodsKillRpcServiceApplication类main方法启动远程服务;

- 编译好整个项目后使用tomcat发布server模块，端口号18080，上下文环境配置为goodsKill,部署成功后访问
http://localhost:18080/goodsKill/login，默认管理员账号admin123，密码：aa123456;

- service和server下的resources/profile用于存放不同环境的配置信息，默认使用local目录的properties配置，如需应用其他环境下的配置文件，[请到此处修改配置](https://github.com/techa03/goodsKill/blob/dev_maven/pom.xml#L27-L55);

- 根据不同环境打包项目在根目录中使用maven命令:
```
mvn clean install -P<你的profileId>
eg:
mvn clean install -Plocal
```
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

- 注意logback.xml和seckill.properties的文件路径配置信息，修改成你自定义的目录即可；

- 可通过http://localhost:18080/goodsKill/swagger-ui.html#/访问swagger主页


#### 错误排查
- 如果发现减少库存无效（update set number=number-1语句），请检查mysql是否为8.0+版本

#### 附：项目目录结构
```
goodsKill
|--.travis.yml
|--goodsKill-api
|   |--pom.xml
|   |--src
|   |   |--main
|   |   |   |--java
|   |   |   |   |--org
|   |   |   |   |   |--seckill
|   |   |   |   |   |   |--api
|   |   |   |   |   |   |   |--annotation
|   |   |   |   |   |   |   |   |--BaseService.java
|   |   |   |   |   |   |   |--constant
|   |   |   |   |   |   |   |   |--SeckillStatusConstant.java
|   |   |   |   |   |   |   |--dto
|   |   |   |   |   |   |   |   |--Exposer.java
|   |   |   |   |   |   |   |   |--SeckillExecution.java
|   |   |   |   |   |   |   |   |--SeckillInfo.java
|   |   |   |   |   |   |   |   |--SeckillResult.java
|   |   |   |   |   |   |   |--enums
|   |   |   |   |   |   |   |   |--SeckillStatEnum.java
|   |   |   |   |   |   |   |   |--UserAccountEnum.java
|   |   |   |   |   |   |   |--exception
|   |   |   |   |   |   |   |   |--CommonException.java
|   |   |   |   |   |   |   |   |--RepeatKillException.java
|   |   |   |   |   |   |   |   |--SeckillCloseException.java
|   |   |   |   |   |   |   |   |--SeckillException.java
|   |   |   |   |   |   |   |--service
|   |   |   |   |   |   |   |   |--CommonService.java
|   |   |   |   |   |   |   |   |--GoodsService.java
|   |   |   |   |   |   |   |   |--PermissionService.java
|   |   |   |   |   |   |   |   |--RolePermissionService.java
|   |   |   |   |   |   |   |   |--RoleService.java
|   |   |   |   |   |   |   |   |--SeckillService.java
|   |   |   |   |   |   |   |   |--UserAccountService.java
|   |   |   |   |   |   |   |   |--UserRoleService.java
|--goodsKill-dao
|   |--pom.xml
|   |--src
|   |   |--main
|   |   |   |--java
|   |   |   |   |--org
|   |   |   |   |   |--seckill
|   |   |   |   |   |   |--dao
|   |   |   |   |   |   |   |--BaseMapper.java
|   |   |   |   |   |   |   |--common
|   |   |   |   |   |   |   |   |--DataSourceEnum.java
|   |   |   |   |   |   |   |   |--DynamicDataSource.java
|   |   |   |   |   |   |   |--ext
|   |   |   |   |   |   |   |   |--ExtSeckillMapper.java
|   |   |   |   |   |   |   |--GoodsMapper.java
|   |   |   |   |   |   |   |--PermissionMapper.java
|   |   |   |   |   |   |   |--RedisDao.java
|   |   |   |   |   |   |   |--RoleMapper.java
|   |   |   |   |   |   |   |--RolePermissionMapper.java
|   |   |   |   |   |   |   |--SeckillMapper.java
|   |   |   |   |   |   |   |--SuccessKilledMapper.java
|   |   |   |   |   |   |   |--UserMapper.java
|   |   |   |   |   |   |   |--UserRoleMapper.java
|   |   |   |--resources
|   |   |   |   |--mapper
|   |   |   |   |   |--ext
|   |   |   |   |   |   |--ExtSeckillMapper.xml
|   |   |   |   |   |--GoodsMapper.xml
|   |   |   |   |   |--PermissionMapper.xml
|   |   |   |   |   |--RoleMapper.xml
|   |   |   |   |   |--RolePermissionMapper.xml
|   |   |   |   |   |--SeckillMapper.xml
|   |   |   |   |   |--SuccessKilledMapper.xml
|   |   |   |   |   |--UserMapper.xml
|   |   |   |   |   |--UserRoleMapper.xml
|   |   |   |   |--message.properties
|   |   |   |   |--message_en_US.properties
|   |   |   |   |--message_zh_CN.properties
|   |   |   |   |--META-INF
|   |   |   |   |   |--spring
|   |   |   |   |   |   |--spring-dao.xml
|   |   |   |   |--mybatis-config.xml
|   |   |--test
|   |   |   |--java
|   |   |   |   |--org
|   |   |   |   |   |--seckill
|   |   |   |   |   |   |--dao
|   |   |   |   |   |   |   |--test
|   |   |   |   |   |   |   |   |--base
|   |   |   |   |   |   |   |   |   |--BaseMapperTestConfig.java
|   |   |   |--resources
|   |   |   |   |--connections_test.properties
|--goodsKill-entry
|   |--pom.xml
|   |--src
|   |   |--main
|   |   |   |--java
|   |   |   |   |--org
|   |   |   |   |   |--seckill
|   |   |   |   |   |   |--entity
|   |   |   |   |   |   |   |--Goods.java
|   |   |   |   |   |   |   |--GoodsExample.java
|   |   |   |   |   |   |   |--Permission.java
|   |   |   |   |   |   |   |--PermissionExample.java
|   |   |   |   |   |   |   |--Role.java
|   |   |   |   |   |   |   |--RoleExample.java
|   |   |   |   |   |   |   |--RolePermission.java
|   |   |   |   |   |   |   |--RolePermissionExample.java
|   |   |   |   |   |   |   |--Seckill.java
|   |   |   |   |   |   |   |--SeckillExample.java
|   |   |   |   |   |   |   |--SuccessKilled.java
|   |   |   |   |   |   |   |--SuccessKilledExample.java
|   |   |   |   |   |   |   |--SuccessKilledKey.java
|   |   |   |   |   |   |   |--User.java
|   |   |   |   |   |   |   |--UserExample.java
|   |   |   |   |   |   |   |--UserRole.java
|   |   |   |   |   |   |   |--UserRoleExample.java
|--goodsKill-generator
|   |--generatorConfig.xml
|   |--pom.xml
|--goodsKill-service
|   |--pom.xml
|   |--src
|   |   |--main
|   |   |   |--java
|   |   |   |   |--org
|   |   |   |   |   |--seckill
|   |   |   |   |   |   |--service
|   |   |   |   |   |   |   |--aop
|   |   |   |   |   |   |   |   |--aspect
|   |   |   |   |   |   |   |   |   |--DataSourceEntryAspect.java
|   |   |   |   |   |   |   |--common
|   |   |   |   |   |   |   |   |--trade
|   |   |   |   |   |   |   |   |   |--alipay
|   |   |   |   |   |   |   |   |   |   |--AlipayRunner.java
|   |   |   |   |   |   |   |--GoodsKillRpcServiceApplication.java
|   |   |   |   |   |   |   |--impl
|   |   |   |   |   |   |   |   |--AbstractServiceImpl.java
|   |   |   |   |   |   |   |   |--GoodsServiceImpl.java
|   |   |   |   |   |   |   |   |--PermissionServiceImpl.java
|   |   |   |   |   |   |   |   |--RolePermissionServiceImpl.java
|   |   |   |   |   |   |   |   |--RoleServiceImpl.java
|   |   |   |   |   |   |   |   |--SeckillServiceImpl.java
|   |   |   |   |   |   |   |   |--UserAccountServiceImpl.java
|   |   |   |   |   |   |   |   |--UserRoleServiceImpl.java
|   |   |   |   |   |   |   |--inner
|   |   |   |   |   |   |   |   |--SeckillExecutor.java
|   |   |   |   |   |   |   |--mq
|   |   |   |   |   |   |   |   |--AbstractMqConsumer.java
|   |   |   |   |   |   |   |   |--MqTask.java
|   |   |   |   |   |   |   |   |--SeckillActiveConsumer.java
|   |   |   |   |   |   |   |   |--SekcillKafkaConsumer.java
|   |   |   |   |   |   |   |--util
|   |   |   |   |   |   |   |   |--ApplicationContextUtil.java
|   |   |   |   |   |   |   |   |--PropertiesUtil.java
|   |   |   |--resources
|   |   |   |   |--lib
|   |   |   |   |--logback.xml
|   |   |   |   |--message.properties
|   |   |   |   |--message_en_US.properties
|   |   |   |   |--message_zh_CN.properties
|   |   |   |   |--META-INF
|   |   |   |   |   |--spring
|   |   |   |   |   |   |--spring-dubbo-provider.xml
|   |   |   |   |   |   |--spring-service-mq.xml
|   |   |   |   |   |   |--spring-service.xml
|   |   |   |   |--mybatis-config.xml
|   |   |   |   |--profile
|   |   |   |   |   |--local
|   |   |   |   |   |   |--connections.properties
|   |   |   |   |   |--prd
|   |   |   |   |   |   |--connections.properties
|   |   |   |   |   |--sit
|   |   |   |   |   |   |--connections.properties
|   |   |   |   |   |   |--logback.xml
|   |   |   |   |   |--uat
|   |   |   |   |   |   |--connections.properties
|   |   |   |   |--zfbinfo.properties
|   |   |--test
|   |   |   |--java
|   |   |   |   |--org
|   |   |   |   |   |--seckill
|   |   |   |   |   |   |--service
|   |   |   |   |   |   |   |--impl
|   |   |   |   |   |   |   |   |--test
|   |   |   |   |   |   |   |   |   |--mock
|   |   |   |   |   |   |   |   |   |   |--GoodsServiceImplTest.java
|   |   |   |   |   |   |   |   |   |   |--SeckillServiceImplTest.java
|   |   |   |   |   |   |   |   |   |   |--UserAccountServiceImplTest.java
|   |   |   |   |   |   |   |--test
|   |   |   |   |   |   |   |   |--base
|   |   |   |   |   |   |   |   |   |--BaseServiceConfigForTest.java
|--goodsKill-util
|   |--pom.xml
|   |--src
|   |   |--main
|   |   |   |--java
|   |   |   |   |--org
|   |   |   |   |   |--seckill
|   |   |   |   |   |   |--util
|   |   |   |   |   |   |   |--common
|   |   |   |   |   |   |   |   |--util
|   |   |   |   |   |   |   |   |   |--AESUtil.java
|   |   |   |   |   |   |   |   |   |--DateUtil.java
|   |   |   |   |   |   |   |   |   |--EncryptPropertyPlaceholderConfigurer.java
|   |   |   |   |   |   |   |   |   |--GenerateProjectTreeUtil.java
|   |   |   |   |   |   |   |   |   |--MD5Util.java
|   |   |   |   |   |   |   |   |   |--PropertiesUtil.java
|   |   |   |   |   |   |   |   |   |--String2DateUtil.java
|   |   |   |--resources
|   |   |   |   |--seckill.properties
|--goodsKill-web
|   |--pom.xml
|   |--src
|   |   |--main
|   |   |   |--java
|   |   |   |   |--org
|   |   |   |   |   |--seckill
|   |   |   |   |   |   |--web
|   |   |   |   |   |   |   |--controller
|   |   |   |   |   |   |   |   |--AdminController.java
|   |   |   |   |   |   |   |   |--AuthcController.java
|   |   |   |   |   |   |   |   |--CommonController.java
|   |   |   |   |   |   |   |   |--GoodsController.java
|   |   |   |   |   |   |   |   |--SeckillController.java
|   |   |   |   |   |   |   |   |--SeckillMockController.java
|   |   |   |   |   |   |   |   |--UserAccountController.java
|   |   |   |   |   |   |   |--dto
|   |   |   |   |   |   |   |   |--PermissionDto.java
|   |   |   |   |   |   |   |   |--ResponseDto.java
|   |   |   |   |   |   |   |   |--RoleDto.java
|   |   |   |   |   |   |   |--mqlistener
|   |   |   |   |   |   |   |   |--SeckillTopicListener.java
|   |   |   |   |   |   |   |--swagger
|   |   |   |   |   |   |   |   |--RestApiConfig.java
|   |   |   |   |   |   |   |--util
|   |   |   |   |   |   |   |   |--RetryLimitHashedCredentialsMatcher.java
|   |   |   |   |   |   |   |   |--UserRealm.java
|   |   |   |--resources
|   |   |   |   |--ehcache.xml
|   |   |   |   |--logback.xml
|   |   |   |   |--profile
|   |   |   |   |   |--local
|   |   |   |   |   |   |--goodsKill-server.properties
|   |   |   |   |   |--prd
|   |   |   |   |   |   |--goodsKill-server.properties
|   |   |   |   |   |--sit
|   |   |   |   |   |   |--goodsKill-server.properties
|   |   |   |   |   |   |--logback.xml
|   |   |   |   |   |--uat
|   |   |   |   |   |   |--goodsKill-server.properties
|   |   |   |   |--spring
|   |   |   |   |   |--spring-dubbo-consumer.xml
|   |   |   |   |   |--spring-shiro-web.xml
|   |   |   |   |   |--spring-web-mq.xml
|   |   |   |   |   |--spring-web.xml
|   |   |   |--sql
|   |   |   |   |--procedure.sql
|   |   |   |   |--seckill.sql
|   |   |   |--webapp
|   |   |   |   |--html
|   |   |   |   |   |--404.html
|   |   |   |   |   |--500.html
|   |   |   |   |   |--admin
|   |   |   |   |   |   |--permission.html
|   |   |   |   |   |   |--role.html
|   |   |   |   |   |   |--user.html
|   |   |   |   |   |   |--userRole.html
|   |   |   |   |   |--css
|   |   |   |   |   |   |--awesomeStyle
|   |   |   |   |   |   |   |--awesome.css
|   |   |   |   |   |   |   |--awesome.less
|   |   |   |   |   |   |   |--fa.less
|   |   |   |   |   |   |   |--img
|   |   |   |   |   |   |   |   |--loading.gif
|   |   |   |   |   |   |--bootstrap.css
|   |   |   |   |   |   |--demo.css
|   |   |   |   |   |   |--font-awesome.min.css
|   |   |   |   |   |   |--metroStyle
|   |   |   |   |   |   |   |--img
|   |   |   |   |   |   |   |   |--line_conn.png
|   |   |   |   |   |   |   |   |--loading.gif
|   |   |   |   |   |   |   |   |--metro.gif
|   |   |   |   |   |   |   |   |--metro.png
|   |   |   |   |   |   |   |--metroStyle.css
|   |   |   |   |   |   |--style.css
|   |   |   |   |   |   |--zTreeStyle
|   |   |   |   |   |   |   |--img
|   |   |   |   |   |   |   |   |--diy
|   |   |   |   |   |   |   |   |   |--1_close.png
|   |   |   |   |   |   |   |   |   |--1_open.png
|   |   |   |   |   |   |   |   |   |--2.png
|   |   |   |   |   |   |   |   |   |--3.png
|   |   |   |   |   |   |   |   |   |--4.png
|   |   |   |   |   |   |   |   |   |--5.png
|   |   |   |   |   |   |   |   |   |--6.png
|   |   |   |   |   |   |   |   |   |--7.png
|   |   |   |   |   |   |   |   |   |--8.png
|   |   |   |   |   |   |   |   |   |--9.png
|   |   |   |   |   |   |   |   |--line_conn.gif
|   |   |   |   |   |   |   |   |--loading.gif
|   |   |   |   |   |   |   |   |--zTreeStandard.gif
|   |   |   |   |   |   |   |   |--zTreeStandard.png
|   |   |   |   |   |   |   |--zTreeStyle.css
|   |   |   |   |   |--fonts
|   |   |   |   |   |   |--fontawesome-webfont.eot
|   |   |   |   |   |   |--fontawesome-webfont.svg
|   |   |   |   |   |   |--fontawesome-webfont.ttf
|   |   |   |   |   |   |--fontawesome-webfont.woff
|   |   |   |   |   |   |--FontAwesome.otf
|   |   |   |   |   |   |--glyphicons-halflings-regular.eot
|   |   |   |   |   |   |--glyphicons-halflings-regular.svg
|   |   |   |   |   |   |--glyphicons-halflings-regular.ttf
|   |   |   |   |   |   |--glyphicons-halflings-regular.woff
|   |   |   |   |   |--index.html
|   |   |   |   |   |--js
|   |   |   |   |   |   |--bootstrap-table-zh-CN.js
|   |   |   |   |   |   |--bootstrap-table.css
|   |   |   |   |   |   |--bootstrap-table.js
|   |   |   |   |   |   |--bootstrap.js
|   |   |   |   |   |   |--config.js
|   |   |   |   |   |   |--countUp.js
|   |   |   |   |   |   |--custom.js
|   |   |   |   |   |   |--custom500.js
|   |   |   |   |   |   |--jquery-1.10.2.js
|   |   |   |   |   |   |--jquery.ztree.all.min.js
|   |   |   |   |--WEB-INF
|   |   |   |   |   |--jsp
|   |   |   |   |   |   |--addGoods.jsp
|   |   |   |   |   |   |--admin
|   |   |   |   |   |   |   |--admin.jsp
|   |   |   |   |   |   |   |--user.jsp
|   |   |   |   |   |   |--common
|   |   |   |   |   |   |   |--common.jsp
|   |   |   |   |   |   |   |--tag.jsp
|   |   |   |   |   |   |--detail.jsp
|   |   |   |   |   |   |--list.jsp
|   |   |   |   |   |   |--login.jsp
|   |   |   |   |   |   |--register.jsp
|   |   |   |   |   |   |--seckill
|   |   |   |   |   |   |   |--addSeckill.jsp
|   |   |   |   |   |   |   |--edit.jsp
|   |   |   |   |   |   |   |--payByQrcode.jsp
|   |   |   |   |   |   |--upload.jsp
|   |   |   |   |   |--web.xml
|--hs_err_pid12412.log
|--LICENSE
|--pom.xml
|--README.md
|--sonar-project.properties
```


