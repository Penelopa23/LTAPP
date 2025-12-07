# Лёгкий образ с Java 21 JRE
FROM eclipse-temurin:21-jre-alpine

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем собранный JAR в контейнер
COPY target/LTAPP-1.0-SNAPSHOT.jar app.jar

# Порт, на котором слушает Spring Boot (у тебя 8080)
EXPOSE 8080

# Переменные по умолчанию — можно не трогать, будем переопределять через docker run / compose
ENV LTAPP_SERVER_PORT=8080

# Старт приложения
ENTRYPOINT ["java", "-jar", "/app/app.jar"]