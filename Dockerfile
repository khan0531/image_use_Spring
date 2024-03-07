FROM openjdk:11-jdk-slim
COPY build/libs/image_use_Spring.jar image-api.jar
ENTRYPOINT ["java", "-DSpring.progiles.avtive=prod","-jar","image-api.jar"]
