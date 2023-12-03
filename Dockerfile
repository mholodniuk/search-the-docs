FROM bellsoft/liberica-openjdk-alpine-musl:17

WORKDIR /app
COPY target/search-the-docs-0.0.1-SNAPSHOT.jar search-the-docs.jar
CMD ["java", "-jar", "search-the-docs.jar"]