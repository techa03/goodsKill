FROM openjdk:11.0.7-jre
COPY target/goodsKill.jar /app/goodsKill.jar
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08", "goodsKill.jar"]

