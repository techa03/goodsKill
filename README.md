# 🎉前言
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java CI with Maven](https://github.com/techa03/goodsKill/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/techa03/goodsKill/actions/workflows/maven.yml)
[![codecov](https://codecov.io/gh/techa03/goodsKill/branch/master/graph/badge.svg)](https://codecov.io/gh/techa03/goodsKill)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=techa03_goodsKill&metric=alert_status)](https://sonarcloud.io/dashboard?id=techa03_goodsKill)

项目命名为**goodsKill**一方面有商品秒杀项目的意思(好像有点chinglish的味道)，另外也可理解为**good skill**，本项目就是希望搭建一套完整的项目框架，把一些好的技术和技巧整合进来（偏向于后端技术），方便学习和查阅。

本项目为慕课网仿购物秒杀系统，项目整体分为用户注册登录、秒杀商品管理模块。使用Shiro完成登录验证授权以及用户权限验证（权限设计使用RBAC模型），支持OAuth2.0第三方授权登录（目前可通过Gitee，Github进行授权）。 技术上整体采用SpringMVC+RESTFUL风格，Mybatis持久层框架，采用SpringCloud Dubbo以及Feign实现服务分布式服务调用，服务注册发现以及配置中心使用Nacos。

本项目在原基础上扩展了部分功能，例如支持数据库分库分表、分布式事务，并提供基本的秒杀解决方案（通过模拟接口实现）。

- 集成Sentinel限流组件，可以针对http请求以及Dubbo RPC调用限流

- 集成服务网关，采用Spring Cloud Gateway网关组件，支持动态路由刷新

## 💎分支介绍
`dev_gradle`分支为使用gradle构建工具管理项目依赖（已停更），`dev_maven`分支对应maven构建工具（springframework版本4.x，已停更），`master`分支基于最新springcloud体系构建。本项目功能目前比较简陋且有很多不完善的地方，仅作学习参考之用，如果觉得本项目对你有帮助的请多多star支持一下👍~~~~。

> 附：码云项目链接[https://gitee.com/techa/goodsKill](https://gitee.com/techa/goodsKill) ,clone速度慢的用码云仓库拉吧，不定期同步到码云~
## ✨技术选型

### 📌后端技术:
| 技术                       | 名称                | 官网                                                                                                   |
|--------------------------|-------------------|------------------------------------------------------------------------------------------------------|
| SpringBoot               | 框架                | [https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot)                     |
| MyBatis-Plus             | MyBatis增强工具       | [https://mp.baomidou.com/](https://mp.baomidou.com/)                                                 |
| Hikari                   | 数据库连接池            | [https://github.com/brettwooldridge/HikariCP](https://github.com/brettwooldridge/HikariCP)           |
| ZooKeeper                | 分布式协调服务           | [http://zookeeper.apache.org/](http://zookeeper.apache.org/)                                         |
| Redis                    | 分布式缓存数据库          | [https://redis.io/](https://redis.io/)                                                               |
| Kafka                    | 消息队列              | [http://kafka.apache.org/](http://kafka.apache.org/)                                                 |
| RabbitMQ                 | 消息队列              | [https://www.rabbitmq.com/](https://www.rabbitmq.com/)                                               |
| Logback                  | 日志组件              | [https://logback.qos.ch/](https://logback.qos.ch/)                                                   |
| Protobuf & json          | 数据序列化             | [https://github.com/google/protobuf](https://github.com/google/protobuf)                             |
| Maven                    | 项目构建管理            | [http://maven.apache.org/](http://maven.apache.org/)                                                 |
| SonarQube                | 项目代码质量监控          | [https://www.sonarqube.org/](https://www.sonarqube.org/)                                             |
| Swagger3                 | 项目API文档生成及测试工具    | [http://swagger.io/](http://swagger.io/)                                                             |
| Mockito                  | mock类生成测试工具       | [https://site.mockito.org/](https://site.mockito.org/)                                               |
| Jacoco                   | 测试覆盖率报告插件         | [http://www.eclemma.org/jacoco/](http://www.eclemma.org/jacoco/)                                     |
| Shiro                    | 用户权限安全管理框架        | [https://shiro.apache.org/](https://shiro.apache.org/)                                               |
| MongoDB                  | Mongo数据库          | [https://www.mongodb.com/](https://www.mongodb.com/)                                                 |
| MySQL                    | MySQL数据库          | [https://www.mysql.com/](https://www.mysql.com/)                                                     |
| Reactor                  | 反应式异步编程           | [https://projectreactor.io/](https://projectreactor.io/)                                             |
| Spring Session           | Spring会话管理        | [https://spring.io/projects/spring-session](https://spring.io/projects/spring-session)               |
| Elasticsearch            | 全文搜索引擎            | [https://www.elastic.co](https://www.elastic.co)                                                     |
| H2                       | H2数据库             | [http://www.h2database.com/html/main.html](http://www.h2database.com/html/main.html)                 |
| Sharding-JDBC            | 分库分表组件            | [https://shardingsphere.apache.org](https://shardingsphere.apache.org)                               |
| Spring Cloud Dubbo       | SpringCloud组件     | [https://spring.io/projects/spring-cloud-alibaba](https://spring.io/projects/spring-cloud-alibaba)   |
| Spring Cloud Sentinel    | SpringCloud限流降级组件 | [https://spring.io/projects/spring-cloud-alibaba](https://spring.io/projects/spring-cloud-alibaba)   |
| Spring Cloud Nacos       | SpringCloud组件     | [https://spring.io/projects/spring-cloud-alibaba](https://spring.io/projects/spring-cloud-alibaba)   |
| Spring Cloud Gateway     | SpringCloud网关组件   | [https://spring.io/projects/spring-cloud-gateway](https://spring.io/projects/spring-cloud-gateway)   |
| Kotlin                   | Kotlin            | [https://kotlinlang.org/](https://kotlinlang.org/)                                                   |
| Seata                    | 分布式事务解决方案         | [http://seata.io/zh-cn/index.html](http://seata.io/zh-cn/index.html)                                 |
| Spring Security OAuth2.0 | OAuth2.0授权组件      | [https://spring.io/projects/spring-security-oauth](https://spring.io/projects/spring-security-oauth) |

### 📌前端技术:
| 技术        | 名称     | 官网                                                   |
|-----------|--------|------------------------------------------------------|
| JQuery    | 函式库    | [http://jquery.com/](http://jquery.com/)             |
| Bootstrap | 前端框架   | [http://getbootstrap.com/](http://getbootstrap.com/) |
| LayUI     | 前端UI框架 | [http://www.layui.com/](http://www.layui.com/)       |

## 📝项目模块介绍
```
goodsKill
|--goodskill-admin                          ||SpringBoot Admin监控服务端，支持Spring Cloud微服务发现
|--goodskill-chat-provider                  ||聊天室服务提供者（待完成）
|--goodskill-common                         ||项目公共服务（目前包括字典服务，待补充）
|--goodskill-gateway                        ||微服务网关，支持动态路由加载
|--goodskill-es-provider                    ||elasticsearch搜索服务提供者，提供商品名称检索功能
|   |--goodskill-es-api                     
|   |--goodskill-es-dao                     
|   |--goodskill-es-service                 
|--goodskill-mongo-provider                 ||mongo存储服务提供者
|   |--goodskill-mongo-service              
|--goodskill-spring-boot-provider           ||订单、用户、登录、商品管理服务提供者（待拆分）
|   |--goodskill-api                        ||提供服务API接口
|   |--goodskill-mp-dao                     ||数据库访问  
|   |--goodskill-entry                      ||实体类
|   |--goodskill-generator                  ||项目代码生成
|   |--goodskill-service                    ||服务API接口实现
|--goodskill-spring-boot-starter            ||项目配置自动装配
|--goodskill-web                            ||提供页面客户端访问，controller层在这一模块   
|--goodskill-job                            ||elastic-job定时任务 
|--goodskill-seata                          ||集成nacos+dubbo+shardingjdbc+seata的分布式事务解决方案示例
|--goodskill-oauth2                         ||oauth2.0自定义模块
    |--oauth2-auth-server                   ||oauth2.0登录授权服务端，自定义的授权登录服务
    |--oauth2-resource-server               ||oauth2.0资源服务端，自定义的授权登录服务
```

## 🔥🔥秒杀方案
目前实现了几种秒杀方案，通过`SeckillMockController`提供测试接口

Swagger主页测试地址: http://www.goodskill.com:8080/goodskill/web/swagger-ui/index.html

SpringBoot Admin应用监控地址: http://www.goodskill.com:8083, 登录用户名密码：admin/123456

Kafka状态监控页面地址: http://localhost:9000

Zipkin链路跟踪页面地址: http://localhost:9411/zipkin/

- 场景一：sychronized同步锁实现
- 场景二：Redisson分布式锁实现
- 场景三：ActiveMQ实现(已废弃)
- 场景四：Kafka消息队列实现
- 场景五：数据库原子性更新
- 场景六：实时等待秒杀处理结果(已废弃)
- 场景七：zookeeper分布式锁
- 场景八：使用redis进行秒杀商品减库存操作，秒杀结束后异步发送MQ，使用mongoDB完成数据落地
- 场景九：SpringCloudStream实现
- 场景十：Sentinel限流+数据库原子性更新（需搭配sentinel控制台配置资源名`limit`的流控规则）

可在web控台查看秒杀结果，打印信息类似：
 ```
2021-04-14 21:58:59.857  INFO [goodskill-web,df43cc8f59291c48,df43cc8f59291c48] 15808 --- [           main] o.s.w.controller.SeckillMockController   : 秒杀场景二(redis分布式锁实现)开始时间：Wed Apr 14 21:58:59 CST 2021,秒杀id：1000
2021-04-14 21:59:00.094  INFO [goodskill-web,144aa7910cca9520,2821cb8d62c5a908] 15808 --- [AClOSzbugzYng-1] o.s.w.s.c.SeckillMockResponseListener    : 秒杀活动结束，秒杀场景二(redis分布式锁实现)时间：Wed Apr 14 21:59:00 CST 2021,秒杀id：1000
2021-04-14 21:59:00.101  INFO [goodskill-web,144aa7910cca9520,2821cb8d62c5a908] 15808 --- [AClOSzbugzYng-1] o.s.w.s.c.SeckillMockResponseListener    : 最终成功交易笔数统计中。。。
2021-04-14 21:59:01.616  INFO [goodskill-web,144aa7910cca9520,2821cb8d62c5a908] 15808 --- [AClOSzbugzYng-1] o.s.w.s.c.SeckillMockResponseListener    : 最终成功交易笔数统计中。。。
2021-04-14 21:59:03.129  INFO [goodskill-web,144aa7910cca9520,2821cb8d62c5a908] 15808 --- [AClOSzbugzYng-1] o.s.w.s.c.SeckillMockResponseListener    : 最终成功交易笔数：10
2021-04-14 21:59:03.130  INFO [goodskill-web,144aa7910cca9520,2821cb8d62c5a908] 15808 --- [AClOSzbugzYng-1] o.s.w.s.c.SeckillMockResponseListener    : 历史任务耗时统计：StopWatch '': running time = 36159894800 ns
---------------------------------------------
ns         %     Task name
---------------------------------------------
4492195700  012%  秒杀场景四(kafka消息队列实现)
3164155900  009%  秒杀场景八(秒杀商品存放redis减库存，异步发送秒杀成功MQ，mongoDb数据落地)
6219218300  017%  秒杀场景十(Sentinel限流+数据库原子性更新)
9189080600  025%  秒杀场景七(zookeeper分布式锁)
3135926500  009%  秒杀场景五(数据库原子性更新update set num = num -1)
3342791800  009%  秒杀场景九(基于springcloud stream rabbitmq)
3343433700  009%  秒杀场景一(sychronized同步锁实现)
3273092300  009%  秒杀场景二(redis分布式锁实现)
 ```

## 🧰开发环境版本说明
- JDK: OpenJDK11
- Sharding-JDBC: 4.1.1
- SpringCloud: 2021.x.x
- SpringBoot: 2.6.x
- SpringCloudAlibaba: 2021.x
- Kotlin: 1.6.x

## 🎯快速开始
- 项目根目录`goodsKill`中执行
  ```
  mvn clean install
  或 
  #跳过单元测试
  mvn clean install -DskipTests
  ```
- 默认端口启动nacos、redis、mysql、rabbitmq、kafka、zookeeper
- 进入<code>goodskill-web/src/main/sql</code>目录，找到<code>seckill.sql</code>文件，在本地mysql数据库中建立<code>seckill</code>仓库并执行完成数据初始化操作
- 配置host
     ```
     127.0.0.1       kafka
     127.0.0.1       nacos
     127.0.0.1       redis
     127.0.0.1       mysql
     127.0.0.1       zookeeper
     127.0.0.1       mongo
     127.0.0.1       elasticsearch
     127.0.0.1       rabbitmq
     127.0.0.1       zipkin
     127.0.0.1       logstash
     ##如果网关服务部署在远程机器，此处改为相应的远程机器ip
     127.0.0.1       www.goodskill.com
     ```
- main方法运行<code>GatewayApplication</code>类(网关服务)
- main方法运行<code>GoodsKillServiceApplication</code>类(订单、用户、登录、商品管理服务提供者)
- main方法运行<code>SampleWebJspApplication</code>类(web服务)
- 启动完成后访问登录页面[http://www.goodskill.com:8080/goodskill/web/login](http://www.goodskill.com:8080/goodskill/web/login)，默认管理员账号admin123，密码：aa123456

## 🕹️️启动完整项目步骤
> #### 方法一：使用Docker镜像构建脚本

- 项目根目录`goodsKill`中执行
  ```
  mvn clean install
  或 
  #跳过单元测试
  mvn clean install -DskipTests
  ```
- 进入项目根目录(确保已安装`compose`以支持`docker-compose`命令)，执行以下命令：
     ```
    docker-compose up （第一次运行只需执行此命令）
    or
    docker-compose up -d //-d后台运行
    or
    docker-compose up -d --build //build重新构建镜像文件，针对项目自定义镜像配置需要修改的情况
    or
    docker-compose up -d --no-recreate //如上次以构建容器，则此次会跳过构建容器
     ```

  **注**:推荐使用<code>docker-compose</code>命令（推荐电脑运行内存16G以上），无需手动下载软件安装包，开箱即用。此命令会自动拉取docker镜像并以默认端口运行

  镜像 | 版本         | 端口 | 用户名密码
    ---|------------|---|---
  Nacos | 2.0.3      | 8848 | 无
  Redis | latest     | 6379 | 无
  Kafka | 2.13-2.7.0 | 9092 | 无
  KafkaManager | latest     | 9000 | 无
  Mongo | 4.4.3      | 27017 | 无
  MySQL | 8.0.22     | 3306 | root:Password123
  Zookeeper | 3.6.2      | 2181 | 无
  Elasticsearch | 7.16.2     | 9200 9300 | 无
  Kibana | 7.16.2     | 5601 | 无
  RabbitMQ | latest     | 5672 15672 | 无
  Zipkin | latest     | 9411 | 无


**注**:除以上镜像外，<code>docker-compose.yml</code>文件还包含项目构建命令，目前暂未列出。
若仅启动以上镜像不启动项目应用，可使用以下命令：
```
docker-compose -f goodskill-simple.yml up -d
```

> ##### ⚠导入项目数据库基础数据 ️
- 找到<code>seckill.sql</code>文件，在本地mysql数据库中建立<code>seckill</code>仓库并执行完成数据初始化操作

  **注**:docker-compose启动方式会自动执行初始化脚本，因此无需执行该步骤

> #### 方法二：使用IDEA运行项目

- 参照快速开始部分配置host

- 如无`docker`运行环境，可参照官网安装上述应用，本地默认端口启动

- 进入<code>goodskill-gateway</code>模块，通过<code>GatewayApplication</code>类main方法启动服务网关

- 找到<code>EsApplication</code>类main方法启动远程服务

- 找到<code>GoodsKillServiceApplication</code>类main方法启动远程服务

- 进入<code>goodskill-web</code>模块根目录，运行命令或直接通过<code>SampleWebJspApplication</code>类main方法启动
     ```
    mvn spring-boot:run -Dspring.profiles.active=docker
     ```

- 如已安装MongoDB，可以main方法启动<code>MongoReactiveApplication</code>，通过使用该服务操作mongo库

> ##### 额外功能（可选）
- 已集成`sentinel`限流组件，支持`nacos`配置中心方式推送限流规则，使用时需启动`sentinel`控制台，并以`18088`端口启动，docker环境暂不支持。
- seata分布式事务测试方法见[Seata分布式事务测试示例运行说明](https://github.com/techa03/goodsKill/tree/master/goodskill-seata/README.md)

## 📦打包部署方法
- 可参考`Dockerfile`文件，如:
```
FROM openjdk:11.0.9.1-jdk
COPY goodskill-service/target/goodskill-service.jar /app/goodskill-service.jar
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08", "goodskill-service.jar"]
```

## ❓常见问题
- 使用idea启动`SampleWebJspApplication`类(goodskill-web模块)时会出现访问不了页面的问题，eclipse无此问题。

  解决办法：配置启动类的工作目录为goodskill-web
- docker es镜像启动失败？

  出现此问题一般为linux环境，运行以下命令即可
  ```
  sysctl -w vm.max_map_count=262144
  ```
  或者修改/etc/sysctl.conf文件，追加以下配置：
  ```
  grep vm.max_map_count /etc/sysctl.conf
  vm.max_map_count=262144
  ```
- 如何使用本项目自定义的OAuth2.0授权服务器进行登录授权

  待完善。。
- 项目集成的各个框架之间目前的兼容性如何，可以参考本项目的配置在生产环境使用吗？

  本项目目前依赖的各个主流框架的版本比较新，尚未经过完整测试，目前仅用于学习。如果要在生产环境使用，建议使用官方推荐的稳定版本。比如目前的Spring Cloud Alibaba Dubbo官方不建议生产上使用，Spring Cloud Alibaba官方推荐的稳定版为2.2.x（本项目使用2021.1版本） ，附[SpringCloudAlibaba兼容版本说明](https://start.aliyun.com/bootstrap.html)

## 📚分库分表情况说明
| 表              | 数据库   | 是否分库                              | 分库字段       | 是否分表                                    | 分表字段       |
|----------------|-------|-----------------------------------|------------|-----------------------------------------|------------|
| success_killed | MySQL | 是（同一服务器中，分为seckill和seckill_01两个库） | seckill_id | 是（分为success_killed_0,success_kill_1两张表） | user_phone |

**注**:其他表均未分库分表，默认使用seckill作为主库

## 🔖服务网关说明
- http://www.goodskill.com/goodskill/mongo/** 对应访问`goodskill-mongo-provider`服务
- http://www.goodskill.com/goodskill/es/** 对应访问`goodskill-es-provider`服务
- http://www.goodskill.com/goodskill/seata/** 对应访问`goodskill-seata`服务
- http://www.goodskill.com/goodskill/common/** 对应访问`goodskill-service-provider`服务

> #### 动态路由配置说明
- 网关路由默认为静态加载，不够灵活。本项目改造后的网关支持动态加载路由配置，修改后实时生效，使用时需要在nacos配置中心添加配置文件，文件名可通过`application.yml`中的`nacos.router.data.id`配置进行修改（默认nacos dataId为`goodskill-gateway-routes`），路由配置文件内容为json数组格式，例如：
```
[ 
    {
        "id": "goodskill-service-provider",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/goodskill/common/**"
                }
            }
        ],
        "filters": [
            {
                "name": "StripPrefix",
                "args": {
                    "_genkey_1": "2"
                }
            }
        ],
        "uri": "lb://goodskill-service-provider"
    }      
]  
```

## 🔨后续更新计划
| 功能                       | 进度  | 完成时间    | 说明                                  |
|--------------------------|-----|---------|-------------------------------------|
| 集成Spring Cloud Alibaba组件 | ✅   | 2020.5  | 目前已集成nacos、sentinel、dubbo、seata组件   |
| 基于配置中心改造项目配置             | ✅   | 2020.7  |                                     |
| 新版支付宝SDK集成               | ✅   | 2020.7  | 使用当面扫完成付款                           |
| 完善JWT用户鉴权，并提供通用服务接口      | ✅   | 2020.12 |                                     |
| 集成分布式事务解决方案              | ✅   | 2021.2  |                                     |
| 增加OAuth2.0授权登录模块         | ✅   | 2021.9  | 增加自定义OAuth2.0授权以及资源服务，并支持第三方授权登录    |
| 集成ELK日志采集                | ✅   | 2021.12 |                                     |
| 聊天室功能                    | ⏳   |         | 使用netty网络通信，maven分支已经实现，master分支待集成 |
| 前后端分离                    | ⏳   |         | 目前前后端全部放在gooskill-web模块，不利于部署       |
| 丰富项目文档                   | ⏳   |         |                                     |

### API接口
![image](./doc/shortcut/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20170623222039.png)

### API接口说明
![image](./doc/shortcut/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20180819224521.png)

## 💻页面展示

#### 登录
![image](./doc/shortcut/img.png)

#### 注册
![image](./doc/shortcut/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701215804.png)

#### 主页
![image](./doc/shortcut/QQ%E6%B5%8F%E8%A7%88%E5%99%A8%E6%88%AA%E5%9B%BE20180701215849.png)

#### 商品查询
> 搜索框下拉商品候选信息基于elasticsearch实现，支持关键词高亮显示

![image](./doc/shortcut/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20200523214633.png)

#### 用户角色权限管理
![image](./doc/shortcut/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20180727155310.png)

#### 商品列表限流
![image](./doc/shortcut/微信截图_20200802182806.png)

#### Kafka状态监控
![image](./doc/shortcut/微信截图_20210130152701.png)

#### 模拟秒杀接口测试
![image](./doc/shortcut/模拟秒杀接口测试.gif)

## 📑数据库表结构
![image](doc/model_table.png)

## 📖参考文档
- 解决Docker容器连接 Kafka 连接失败问题：https://www.cnblogs.com/hellxz/p/why_cnnect_to_kafka_always_failure.html
