FROM openjdk:11.0.9.1-jdk
COPY goodsKill-web/target/goodsKill.war /app/goodsKill.war
# copy arthas
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08","-Xms512m","-Xmx1536m","-XX:+HeapDumpOnOutOfMemoryError", "goodsKill.war"]