FROM java:8-jre-alpine
COPY goodsKill-web/target/goodsKill.jar /app/goodsKill.jar
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08", "goodsKill.jar"]