#Dockerfile
# Compilacion dentro de Docker
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Imagen ligera para despliegue
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos el jar desde la etapa builder
COPY --from=builder /app/target/techstore-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]