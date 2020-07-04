FROM openjdk:11.0.7-jre
COPY goodskill-mongo-service/target/goodsKill-mongo-service.jar /app/goodsKill-mongo-service.jar
WORKDIR /app
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "-Duser.timezone=GMT+08", "goodsKill-mongo-service.jar"]
#java -jar -Dspring.profiles.active=docker -Duser.timezone=GMT+08 goodsKill-mongo-service.jar