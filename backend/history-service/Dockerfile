# First Stage - Build and package our app
FROM eclipse-temurin:25-noble AS build
WORKDIR /app
COPY ./ ./ 
RUN ./mvnw dependency:go-offline
RUN ./mvnw package -DskipTests 

# Second Stage - Run the packaged app
FROM eclipse-temurin:25-jre-alpine AS run
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app/app.jar"]

EXPOSE 8085
