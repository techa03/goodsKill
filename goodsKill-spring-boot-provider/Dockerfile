FROM openjdk:11.0.9.1-jdk
COPY goodsKill-service/target/goodsKill-service.jar /app/goodsKill-service.jar
# copy arthas
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08", "goodsKill-service.jar"]

