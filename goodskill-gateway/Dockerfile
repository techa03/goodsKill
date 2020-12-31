FROM openjdk:11.0.9.1-jdk
COPY target/goodsKill.jar /app/goodsKill.jar
# copy arthas
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08","-Xms256m","-Xmx512m","-XX:+HeapDumpOnOutOfMemoryError", "goodsKill.jar"]

