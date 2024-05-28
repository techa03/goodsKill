# seata分布式事务测试方法
- 在nacos控制台新增命名空间，指定id为`bca6fe13-8ddd-4721-b753-022fe3beabd8`
- 在新创建的命名空间下新增dataId `seataServer.properties`并使用以下配置（group需配置为SEATA_GROUP）
  ```
  store.mode=db
  #-----db-----
  store.db.datasource=druid
  store.db.dbType=mysql
  # 需要根据mysql的版本调整driverClassName
  # mysql8及以上版本对应的driver：com.mysql.cj.jdbc.Driver
  # mysql8以下版本的driver：com.mysql.jdbc.Driver
  store.db.driverClassName=com.mysql.cj.jdbc.Driver
  store.db.url=jdbc:mysql://mysql:3306/seata_server?useUnicode=true&characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false
  store.db.user=root
  store.db.password=Password123
  # 数据库初始连接数
  store.db.minConn=1
  # 数据库最大连接数
  store.db.maxConn=20
  # 获取连接时最大等待时间 默认5000，单位毫秒
  store.db.maxWait=5000
  # 全局事务表名 默认global_table
  store.db.globalTable=global_table
  # 分支事务表名 默认branch_table
  store.db.branchTable=branch_table
  # 全局锁表名 默认lock_table
  store.db.lockTable=lock_table
  # 查询全局事务一次的最大条数 默认100
  store.db.queryLimit=100
  
  
  # undo保留天数 默认7天,log_status=1（附录3）和未正常清理的undo
  server.undo.logSaveDays=7
  # undo清理线程间隔时间 默认86400000，单位毫秒
  server.undo.logDeletePeriod=86400000
  # 二阶段提交重试超时时长 单位ms,s,m,h,d,对应毫秒,秒,分,小时,天,默认毫秒。默认值-1表示无限重试
  # 公式: timeout>=now-globalTransactionBeginTime,true表示超时则不再重试
  # 注: 达到超时时间后将不会做任何重试,有数据不一致风险,除非业务自行可校准数据,否者慎用
  server.maxCommitRetryTimeout=-1
  # 二阶段回滚重试超时时长
  server.maxRollbackRetryTimeout=-1
  # 二阶段提交未完成状态全局事务重试提交线程间隔时间 默认1000，单位毫秒
  server.recovery.committingRetryPeriod=1000
  # 二阶段异步提交状态重试提交线程间隔时间 默认1000，单位毫秒
  server.recovery.asynCommittingRetryPeriod=1000
  # 二阶段回滚状态重试回滚线程间隔时间  默认1000，单位毫秒
  server.recovery.rollbackingRetryPeriod=1000
  # 超时状态检测重试线程间隔时间 默认1000，单位毫秒，检测出超时将全局事务置入回滚会话管理器
  server.recovery.timeoutRetryPeriod=1000
  ```
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
