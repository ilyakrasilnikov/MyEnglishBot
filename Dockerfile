FROM openjdk:17
ENV TZ="Asia/Tbilisi"
COPY target/*.jar myEnglishBot.jar
ENTRYPOINT ["java","-jar","/myEnglishBot.jar"]
