# Use Java 21 (Matches your pom.xml)
FROM eclipse-temurin:21-jdk-alpine

# Install necessary libraries for the OS
RUN apk add --no-cache fontconfig ttf-dejavu

WORKDIR /app

# Copy the project files
COPY . .

# Grant permission and build the application
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Run the app.
# NOTE: We use a wildcard *.jar to avoid "file not found" errors if the name changes
CMD ["sh", "-c", "java -jar target/*.jar"]