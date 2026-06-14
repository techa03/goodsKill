# 前言
项目命名为 **goodsKill**
一方面有商品秒杀的意思(好像有点chinglish的味道)，另外也可理解为 **good
skill**，本项目就是希望搭建一套完整的项目框架，把一些好的技术和开发技巧整合进来（偏向于后端技术），方便学习和查阅。

本项目为模拟秒杀项目，提供统一秒杀模拟请求接口，技术上整体采用SpringMVC +
Mybatis持久层框架，采用Dubbo3.x[1]，服务注册发现以及配置中心使用Nacos，支持数据库分库分表、分布式事务，使用状态机完成数据状态间的转换（基于Spring
Statemachine实现）。

## 💎 分支介绍

`master`分支基于最新Spring Cloud 2025.x + Spring Boot 4.x +
JDK21体系构建，目前仅保留核心的模拟秒杀API接口，如需使用Spring Boot
2.7.x + JDK11版本可以切换到tag
[v2.7.4](https://github.com/techa03/goodsKill/tree/v2.7.4)（支持登录注册以及简单的后台管理功能）。master分支目前升级改造中，功能不够稳定，如遇代码报错建议使用老版本。

本项目功能目前比较简陋且有很多不完善的地方，真实的秒杀场景远比本项目中的实现方式复杂，本项目省略了真实场景中的部分技术实现细节，目前仅作学习参考之用，如果觉得本项目对你有帮助的请多多star支持一下👍<sub>~</sub>~。

> 附：码云项目链接
> `https://gitee.com/techa/goodsKill`,clone速度慢的用码云仓库拉吧，不定期同步到码云~

## ✨ 技术选型

<table>
<colgroup>
<col style="width: 33%" />
<col style="width: 33%" />
<col style="width: 33%" />
</colgroup>
<thead>
<tr class="header">
<th style="text-align: left;">使用的工具或框架</th>
<th style="text-align: left;">名称</th>
<th style="text-align: left;">官网</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td style="text-align: left;"><p>Spring Boot</p></td>
<td style="text-align: left;"><p>Spring Boot框架</p></td>
<td style="text-align: left;"><p><a
href="https://spring.io/projects/spring-boot">https://spring.io/projects/spring-boot</a></p></td>
</tr>
<tr class="even">
<td style="text-align: left;"><p>MyBatis-Plus</p></td>
<td style="text-align: left;"><p>MyBatis增强工具</p></td>
<td style="text-align: left;"><p><a
href="https://mp.baomidou.com/">https://mp.baomidou.com/</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>ZooKeeper</p></td>
<td style="text-align: left;"><p>分布式协调服务</p></td>
<td style="text-align: left;"><p><a
href="http://zookeeper.apache.org/">http://zookeeper.apache.org/</a></p></td>
</tr>
<tr class="even">
<td style="text-align: left;"><p>Redis</p></td>
<td style="text-align: left;"><p>分布式缓存数据库</p></td>
<td style="text-align: left;"><p><a
href="https://redis.io/">https://redis.io/</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>Kafka</p></td>
<td style="text-align: left;"><p>消息队列</p></td>
<td style="text-align: left;"><p><a
href="http://kafka.apache.org/">http://kafka.apache.org/</a></p></td>
</tr>
<tr class="even">
<td style="text-align: left;"><p>RabbitMQ</p></td>
<td style="text-align: left;"><p>消息队列</p></td>
<td style="text-align: left;"><p><a
href="https://www.rabbitmq.com/">https://www.rabbitmq.com/</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>MongoDB</p></td>
<td style="text-align: left;"><p>Mongo数据库</p></td>
<td style="text-align: left;"><p><a
href="https://www.mongodb.com/">https://www.mongodb.com/</a></p></td>
</tr>
<tr class="even">
<td style="text-align: left;"><p>MySQL</p></td>
<td style="text-align: left;"><p>MySQL数据库</p></td>
<td style="text-align: left;"><p><a
href="https://www.mysql.com/">https://www.mysql.com/</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>Elasticsearch</p></td>
<td style="text-align: left;"><p>全文搜索引擎</p></td>
<td style="text-align: left;"><p><a
href="https://www.elastic.co">https://www.elastic.co</a></p></td>
</tr>
<tr class="even">
<td style="text-align: left;"><p>Sharding-JDBC</p></td>
<td style="text-align: left;"><p>分库分表组件</p></td>
<td style="text-align: left;"><p><a
href="https://shardingsphere.apache.org">https://shardingsphere.apache.org</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>Spring Cloud Alibaba</p></td>
<td style="text-align: left;"><p>Cloud Alibaba组件</p></td>
<td style="text-align: left;"><p><a
href="https://github.com/alibaba/spring-cloud-alibaba">https://github.com/alibaba/spring-cloud-alibaba</a></p></td>
</tr>
<tr class="even">
<td style="text-align: left;"><p>Apache Dubbo</p></td>
<td style="text-align: left;"><p>RPC服务远程调用框架</p></td>
<td style="text-align: left;"><p><a
href="https://github.com/apache/dubbo">https://github.com/apache/dubbo</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>Spring Cloud Gateway</p></td>
<td style="text-align: left;"><p>网关组件</p></td>
<td style="text-align: left;"><p><a
href="https://spring.io/projects/spring-cloud-gateway">https://spring.io/projects/spring-cloud-gateway</a></p></td>
</tr>
<tr class="even">
<td style="text-align: left;"><p>Seata</p></td>
<td style="text-align: left;"><p>分布式事务解决方案</p></td>
<td style="text-align: left;"><p><a
href="http://seata.io/zh-cn/index.html">http://seata.io/zh-cn/index.html</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>GraphQL</p></td>
<td style="text-align: left;"><p>一种用于 API 的查询语言</p></td>
<td style="text-align: left;"><p><a
href="https://docs.spring.io/spring-graphql/docs/current/reference/html">https://docs.spring.io/spring-graphql/docs/current/reference/html</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>Spring Statemachine</p></td>
<td style="text-align: left;"><p>Spring 状态机</p></td>
<td style="text-align: left;"><p><a
href="https://spring.io/projects/spring-statemachine">https://spring.io/projects/spring-statemachine</a></p></td>
</tr>
<tr class="even">
<td style="text-align: left;"><p>Sa-Token</p></td>
<td style="text-align: left;"><p>轻量级权限认证框架</p></td>
<td style="text-align: left;"><p><a
href="https://sa-token.cc/">https://sa-token.cc/</a></p></td>
</tr>
<tr class="odd">
<td style="text-align: left;"><p>Flyway</p></td>
<td style="text-align: left;"><p>数据库版本控制工具</p></td>
<td style="text-align: left;"><p><a
href="https://flywaydb.org/">https://flywaydb.org/</a></p></td>
</tr>
</tbody>
</table>

## 📝 项目模块介绍

    goodsKill
    |--goodskill-admin                          ||SpringBoot Admin监控服务端，支持Spring Cloud微服务发现
    |--goodskill-gateway                        ||微服务API网关，统一服务鉴权，支持动态路由加载
    |--goodskill-order-provider                 ||订单服务提供者
    |   |--goodskill-order-service
    |--goodskill-seckill-provider               ||秒杀服务提供者
    |   |--goodskill-api                        ||提供服务API接口
    |   |--goodskill-service                    ||服务API接口实现
    |--goodskill-spring-boot-starter            ||项目配置自动装配
    |--goodskill-web                            ||提供秒杀模拟接口访问
    |--goodskill-job                            ||elastic-job定时任务
    |--goodskill-seata                          ||集成nacos+dubbo+shardingjdbc+seata的分布式事务解决方案示例
    |--goodskill-auth                           ||auth登录以及授权模块
    |   |--auth-service                         ||基于Sa-Token框架的用户登录授权服务

## 🔥🔥 秒杀方案

目前实现了几种秒杀方案，通过`SeckillMockController`提供测试接口

聚合网关Openapi文档地址:
`http://localhost/doc.html#/home`（需要开启网关服务）

Spring Boot Admin应用监控地址: `http://www.goodskill.com:19031`,
登录用户名密码：user/123456

-   场景一：Sychronized同步锁实现

-   场景二：Redisson分布式锁实现

-   场景三：ActiveMQ实现(已废弃)

-   场景四：Kafka消息队列实现

-   场景五：数据库原子性更新

-   场景六：实时等待秒杀处理结果(已废弃)

-   场景七：ZooKeeper分布式锁

-   场景八：使用Redis进行秒杀商品减库存操作，秒杀结束后异步发送MQ，使用MongoDB完成数据落地

-   场景九：Spring Cloud Stream实现

-   场景十：Sentinel限流+数据库原子性更新（需搭配sentinel控制台配置资源名`limit`的流控规则）

<!-- -->

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

## 🧰 开发环境版本说明

-   JDK: OpenJDK21

-   Sharding-JDBC: 5.5.0

-   SpringCloud: 2025.1.0

-   SpringBoot: 4.0.0

-   SpringCloudAlibaba: 2025.1.0.0

-   Apache Dubbo: 3.x

-   使用的Docker镜像

    <table>
    <colgroup>
    <col style="width: 25%" />
    <col style="width: 25%" />
    <col style="width: 25%" />
    <col style="width: 25%" />
    </colgroup>
    <thead>
    <tr class="header">
    <th style="text-align: left;">镜像</th>
    <th style="text-align: left;">版本</th>
    <th style="text-align: left;">端口</th>
    <th style="text-align: left;">用户名密码</th>
    </tr>
    </thead>
    <tbody>
    <tr class="odd">
    <td style="text-align: left;"><p>Nacos</p></td>
    <td style="text-align: left;"><p>3.1.1</p></td>
    <td style="text-align: left;"><p>8848</p></td>
    <td style="text-align: left;"><p>nacos:nacos</p></td>
    </tr>
    <tr class="even">
    <td style="text-align: left;"><p>Redis</p></td>
    <td style="text-align: left;"><p>latest</p></td>
    <td style="text-align: left;"><p>6379</p></td>
    <td style="text-align: left;"><p>密码:123456</p></td>
    </tr>
    <tr class="odd">
    <td style="text-align: left;"><p>Kafka</p></td>
    <td style="text-align: left;"><p>3.1.1</p></td>
    <td style="text-align: left;"><p>9092</p></td>
    <td style="text-align: left;"><p>无</p></td>
    </tr>
    <tr class="even">
    <td style="text-align: left;"><p>KafkaManager</p></td>
    <td style="text-align: left;"><p>latest</p></td>
    <td style="text-align: left;"><p>9001:9000</p></td>
    <td style="text-align: left;"><p>无</p></td>
    </tr>
    <tr class="odd">
    <td style="text-align: left;"><p>Mongo</p></td>
    <td style="text-align: left;"><p>6.0.7</p></td>
    <td style="text-align: left;"><p>27017</p></td>
    <td style="text-align: left;"><p>无</p></td>
    </tr>
    <tr class="even">
    <td style="text-align: left;"><p>MySQL</p></td>
    <td style="text-align: left;"><p>8.0.29</p></td>
    <td style="text-align: left;"><p>3306</p></td>
    <td style="text-align: left;"><p>root:Password123</p></td>
    </tr>
    <tr class="odd">
    <td style="text-align: left;"><p>Zookeeper</p></td>
    <td style="text-align: left;"><p>3.6.2</p></td>
    <td style="text-align: left;"><p>2181</p></td>
    <td style="text-align: left;"><p>无</p></td>
    </tr>
    <tr class="even">
    <td style="text-align: left;"><p>Elasticsearch</p></td>
    <td style="text-align: left;"><p>9.2.2</p></td>
    <td style="text-align: left;"><p>9200 9300</p></td>
    <td style="text-align: left;"><p>无</p></td>
    </tr>
    <tr class="odd">
    <td style="text-align: left;"><p>Kibana</p></td>
    <td style="text-align: left;"><p>9.2.2</p></td>
    <td style="text-align: left;"><p>5601</p></td>
    <td style="text-align: left;"><p>无</p></td>
    </tr>
    <tr class="even">
    <td style="text-align: left;"><p>RabbitMQ</p></td>
    <td style="text-align: left;"><p>latest</p></td>
    <td style="text-align: left;"><p>5672 15672</p></td>
    <td style="text-align: left;"><p>无</p></td>
    </tr>
    </tbody>
    </table>


## 📚分库分表情况说明

<table style="width:100%;">
<colgroup>
<col style="width: 16%" />
<col style="width: 16%" />
<col style="width: 16%" />
<col style="width: 16%" />
<col style="width: 16%" />
<col style="width: 16%" />
</colgroup>
<thead>
<tr class="header">
<th style="text-align: left;">表</th>
<th style="text-align: left;">数据库</th>
<th style="text-align: left;">是否分库</th>
<th style="text-align: left;">分库字段</th>
<th style="text-align: left;">是否分表</th>
<th style="text-align: left;">分表字段</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td style="text-align: left;"><p>success_killed</p></td>
<td style="text-align: left;"><p>MySQL</p></td>
<td
style="text-align: left;"><p>是（同一服务器中，分为seckill和seckill_01两个库）</p></td>
<td style="text-align: left;"><p>seckill_id</p></td>
<td
style="text-align: left;"><p>是（分为success_killed_0,success_kill_1两张表）</p></td>
<td style="text-align: left;"><p>user_phone</p></td>
</tr>
</tbody>
</table>

其他表均未分库分表，默认使用seckill作为主库

### API接口说明

![image](shortcut/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20180819224521.png)

## 💻相关页面展示

### 模拟秒杀接口测试

![image](shortcut/模拟秒杀接口测试.gif)

## 📑数据库表结构

![image](shortcut/model_table.png)

## 📖参考文档

-   解决Docker容器连接 Kafka
    连接失败问题：`https://www.cnblogs.com/hellxz/p/why_cnnect_to_kafka_always_failure.html`

[1] 由于SpringCloudAlibaba官方暂未支持Dubbo
3.x，本项目采用dubbo-spring-boot-starter集成

[2] 需要安装docker-desktop
<https://www.docker.com/products/docker-desktop/>

[3] 附
[SpringCloudAlibaba](https://start.aliyun.com/bootstrap.html)兼容版本说明
