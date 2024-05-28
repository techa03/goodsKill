**Q:** docker es镜像启动失败

**A:** 出现此问题一般为linux环境，运行以下命令即可
`sysctl -w vm.max_map_count=262144`
，或者修改/etc/sysctl.conf文件，追加以下配置：

    grep vm.max_map_count /etc/sysctl.conf
    vm.max_map_count=262144

**Q:** 如何使用本项目自定义的OAuth2.0授权服务器进行登录授权？

**A:** 待完善。。

**Q:** 项目集成的各个框架之间目前的兼容性如何？

**A:** 本项目目前依赖的各个主流框架的版本比较新，尚未经过完整测试[3]。

**Q:** 服务启动控制台报ERROR日志
`no available service found in cluster 'default', please make sure registry config correct and keep your seata server running`
如何解决？

**A:** 启动`seata-server`服务即可（docker-compose.yml文件中已提供），可参照Seata官网添加nacos相关配置。如未使用分布式事务，可忽略该错误，不影响服务正常运行

**Q:** docker-compose无法拉取镜像

**A:** hub.docker被墙，国内可使用阿里云镜像加速器，具体操作见
[阿里云镜像加速器](https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors)

**Q:** 使用JDK17以上启动项目失败

**A:** 启动时添加以下jvm参数，例如：

    --add-opens java.base/java.lang=ALL-UNNAMED
    --add-opens java.base/java.util=ALL-UNNAMED
    --add-opens java.base/java.util.concurrent=ALL-UNNAMED
    --add-opens java.base/java.math=ALL-UNNAMED
    --add-opens java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED
