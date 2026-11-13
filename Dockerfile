# Etapa 1: Compilación con Maven y Java 25 (versión definida en pom.xml)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen ligera de ejecución
FROM eclipse-temurin:25-jre
WORKDIR /app

ENV PORT=8080
EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "SERVER_PORT=${PORT:-8080} exec java -Dserver.port=${PORT:-8080} -jar app.jar"]
