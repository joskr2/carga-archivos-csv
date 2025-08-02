FROM amazoncorretto:17-alpine

# Update package index and upgrade packages to fix vulnerabilities
RUN apk update && apk upgrade --no-cache && rm -rf /var/cache/apk/*

WORKDIR /app
COPY target/pedidos-ms-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
