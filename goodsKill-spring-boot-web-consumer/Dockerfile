FROM openjdk:11.0.7-jre
COPY goodsKill-web/target/goodsKill.war /app/goodsKill.war
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08", "goodsKill.war"]