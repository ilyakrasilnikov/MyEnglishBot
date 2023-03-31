FROM openjdk:17
ENV TZ="Asia/Tbilisi"
COPY target/*.jar perfectChat.jar
ENTRYPOINT ["java","-jar","/perfectChat.jar"]
