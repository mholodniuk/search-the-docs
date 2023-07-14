FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /app
COPY target/search-me-daddy-0.0.1-SNAPSHOT.jar search-me-daddy.jar
CMD ["java", "-jar", "search-me-daddy.jar"]