FROM openjdk:17-alpine

RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apk del tzdata

ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} /app.jar

ENTRYPOINT [ \
  "java", \
  "-Dspring.profiles.active=${PROFILES}", \
  "-jar", \
  "/app.jar" \
]
