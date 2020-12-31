FROM openjdk:11.0.9.1-jdk
COPY goodskill-mongo-service/target/goodsKill-mongo-service.jar /app/goodsKill-mongo-service.jar
# copy arthas
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
WORKDIR /app
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "-Duser.timezone=GMT+08","-Xms128m","-Xmx400m","-XX:+HeapDumpOnOutOfMemoryError", "goodsKill-mongo-service.jar"]