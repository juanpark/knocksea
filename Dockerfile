# Use an OpenJDK 21 base image with a smaller footprint
FROM eclipse-temurin:21-jdk-alpine

# Set environment variables
ENV TZ=Asia/Seoul
ENV JAVA_OPTS=""

# Create a directory for the app
WORKDIR /app

# Copy the built JAR file into the container
# The JAR name must match the output of your Maven build
COPY target/knocksea-0.0.1-SNAPSHOT.jar app.jar

# Expose port if you’re running locally or directly in AWS EC2 (e.g., 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
