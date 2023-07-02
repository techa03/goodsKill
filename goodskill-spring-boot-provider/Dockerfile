FROM openjdk:17.0.2-jdk
COPY goodskill-service/target/goodskill-service.jar /app/goodskill-service.jar
# copy arthas
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas
WORKDIR /app
CMD ["java", "-jar","-Dspring.profiles.active=docker","-Duser.timezone=GMT+08", "goodskill-service.jar"]

