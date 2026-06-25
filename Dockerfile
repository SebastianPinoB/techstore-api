# BUILD
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


# RUNTIME
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Crea usuario no root 
RUN addgroup -S spring && adduser -S spring -G spring
# Copia jar desde builder
COPY --from=builder /app/target/*.jar app.jar
# Usar usuario no-root
USER spring
# Exponer puerto
EXPOSE 8080
# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]