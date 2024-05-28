### 发送一个秒杀模拟请求：

秒杀活动id 1000，商品数量10，执行20次购买操作，使用sychronized同步锁执行，例如：

#### 使用以下命令发送模拟秒杀请求：

每个秒杀活动seckillId对应唯一的一个商品id，每次执行接口时会有一个库存初始化动作，接口执行完成后可重复调用

#### 使用sychronized同步锁执行
```bash
curl -X POST "http://www.goodskill.com:8080/sychronized" \
-H "accept: */*" -H "Content-Type: application/json" -d \
"{ \"requestCount\": 20, \"seckillCount\": 10, \"seckillId\": 1000}"
```

#### 使用Redisson分布式锁执行
```bash
curl -X POST "http://www.goodskill.com:8080/redisson" \
-H "accept: */*" -H "Content-Type: application/json" -d \
"{ \"requestCount\": 20, \"seckillCount\": 10, \"seckillId\": 1000}"
```

#### 使用Redisson分布式锁执行，支持动态配置后台线程池核心线程数以及最大线程数
```bash
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
```

请求默认异步执行，可在控制台查看执行日志，如果最终成功交易笔数等于商品数量10则表示没有出现超卖或者少卖问题。
