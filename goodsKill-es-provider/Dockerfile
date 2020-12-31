FROM openjdk:11.0.9.1-jdk
COPY goodskill-es-service/target/goodskill-es-service.jar /app/goodskill-es-service.jar
# copy arthas
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
WORKDIR /app
CMD ["java","-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08","-Xms64m","-Xmx256m","-XX:+HeapDumpOnOutOfMemoryError","goodskill-es-service.jar"]
