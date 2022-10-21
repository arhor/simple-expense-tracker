FROM openjdk:17-jdk-alpine

COPY build/libs/simple-expense-tracker.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
