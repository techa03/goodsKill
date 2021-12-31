FROM openjdk:11.0.9.1-jdk
COPY target/goodskill.jar /app/goodskill.jar
# copy arthas
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08","-Xms64m","-Xmx200m","-XX:+HeapDumpOnOutOfMemoryError", "goodskill.jar"]

