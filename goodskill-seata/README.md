# seata分布式事务测试方法
- 通过项目中根目录的docker-compose.yml文件创建一个seata-server容器
  ```
  docker-compose up -d seata-server
  ```
  **注意**:修改environment SEATA_IP ，不配置则默认使用容器ip，会导致服务连不上seata server
- 在nacos控制台中增加以下配置（group需配置为SEATA_GROUP，dataId为对应key，配置文件内容为value）
  ```
  service.vgroupMapping.my_test_tx_group=default
  store.mode=file
  ```
  可参考Seata官方Nacos配置文档:[http://seata.io/zh-cn/docs/user/configuration/nacos.html](http://seata.io/zh-cn/docs/user/configuration/nacos.html)
- 按顺序启动GoodsKillServiceApplication、GoodskillSeataApplication（注意开启seata事务，配置seata.enabled=true），控制台不报error则启动成功
- 浏览器访问:http://localhost:8080/test ，即可触发测试用例，可根据需要自行增加异常，测试分布式事务回滚情况
