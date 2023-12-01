FROM amazoncorretto:17
WORKDIR /app
COPY build/libs/cloud-service-backend.jar cloud-service-backend.jar
EXPOSE 8081
CMD ["java", "-jar", "cloud-service-backend.jar"]