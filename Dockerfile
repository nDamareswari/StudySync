FROM openjdk:17
WORKDIR /app
COPY . .

# give permission to mvnw
RUN chmod +x mvnw

# build project
RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/studysync-0.0.1-SNAPSHOT.jar"]