-   项目根目录`goodsKill`中执行

          mvn clean install
          或
          #跳过单元测试
          mvn clean install -DskipTests

-   默认端口启动nacos、redis、mysql、rabbitmq、kafka、zookeeper、elasticsearch，或者使用docker-compose[2]命令：

          docker-compose -f goodskill-simple.yml up -d

-   进入`goodskill-web/src/main/sql`目录，找到`seckill.sql`文件，在本地mysql数据库中建立`seckill`仓库并执行完成数据初始化操作

    docker-compose启动MySQL镜像时会自动执行初始化脚本，如已执行过上一步本步骤可跳过

-   配置host

         127.0.0.1       kafka
         127.0.0.1       nacos
         127.0.0.1       redis
         127.0.0.1       mysql
         127.0.0.1       zookeeper
         127.0.0.1       mongo
         127.0.0.1       elasticsearch
         127.0.0.1       rabbitmq
         127.0.0.1       logstash
         ##如果网关服务部署在远程机器，此处改为相应的远程机器ip
         127.0.0.1       www.goodskill.com

-   main方法运行`OrderApplication`类(订单服务)

-   main方法运行`SeckillApplication`类(秒杀管理服务提供者)

-   main方法运行`SampleWebApplication`类(模拟秒杀web服务)

-   发送一个秒杀模拟请求： 秒杀活动id
    1000，商品数量10，执行20次购买操作，使用sychronized同步锁执行，例如：

    可直接使用以下命令发送模拟秒杀请求，每个秒杀活动seckillId对应唯一的一个商品id，每次执行接口时会有一个库存初始化动作，接口执行完成后可重复调用

        curl -X POST "http://www.goodskill.com:8080/sychronized" \
        -H "accept: */*" -H "Content-Type: application/json" -d \
        "{ \"requestCount\": 20, \"seckillCount\": 10, \"seckillId\": 1000}"

        curl -X POST "http://www.goodskill.com:8080/redisson" \
        -H "accept: */*" -H "Content-Type: application/json" -d \
        "{ \"requestCount\": 20, \"seckillCount\": 10, \"seckillId\": 1000}"

        curl --location --request POST 'http://www.goodskill.com:8080/limit' \
        --header 'User-Agent: apifox/1.0.0 (https://www.apifox.cn)' \
        --header 'Content-Type: application/json' \
        --data-raw '{
            "maxPoolSize": 10,
            "seckillId": 1000,
            "corePoolSize": 2,
            "seckillCount": 100,
            "requestCount": 120
        }'

    请求默认异步执行，可在控制台查看执行日志，如果最终成功交易笔数等于商品数量10则表示没有出现超卖或者少卖问题

## 🕹️️ 启动其他可选项目步骤

在快速开始部分基础上增加以下步骤即可启动一个完整项目

-   进入`goodskill-gateway`模块，通过`GatewayApplication`类main方法启动服务网关，统一通过网关访问各个服务

    -   `http://www.goodskill.com/api/order/**`
        对应访问`goodskill-order`服务

    -   `http://www.goodskill.com/api/seata/**`
        对应访问`goodskill-seata`服务

    -   `http://www.goodskill.com/api/seckill/**`
        对应访问`goodskill-seckill`服务

    -   `http://www.goodskill.com/api/auth/**`
        对应访问`goodskill-auth`服务

    -   `http://www.goodskill.com/api/web/**`
        对应访问`goodskill-web`服务

-   已集成`Sentinel`限流组件，支持`nacos`配置中心方式推送限流规则，使用时需启动`Sentinel`控制台，并以`18088`端口启动，docker环境暂不支持。

-   Seata分布式事务测试方法见
    [Seata分布式事务测试示例运行说明](https://github.com/techa03/goodsKill/tree/master/goodskill-seata/README.md)

-   main方法运行`GoodskillAdminApplication`类(微服务健康状态指标监控)
