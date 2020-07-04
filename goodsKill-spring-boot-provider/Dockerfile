FROM openjdk:11.0.7-jre
COPY goodsKill-service/target/goodsKill-service.jar /app/goodsKill-service.jar
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08", "goodsKill-service.jar"]

