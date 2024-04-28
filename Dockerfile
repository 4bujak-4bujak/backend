트FROM openjdk:17-alpine
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT [ \
  "java", \
  "-Dspring.profiles.active=${PROFILES}", \
  "-jar", \
  "app.jar" \
]
