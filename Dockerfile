FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /work
COPY . .
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-jammy
LABEL authors="sh-kang"
WORKDIR /app
COPY --from=builder /work/build/libs/coc*.jar /app/coc-layout-bot.jar

ENV TZ=Asia/Seoul
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "coc-layout-bot.jar"]
