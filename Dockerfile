# Этап сборки
FROM gradle:8.13-jdk21 AS build
WORKDIR /app

# Копирование файлов gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Скачивание зависимостей (кэширование)
RUN gradle dependencies --no-daemon

# Копирование исходников и сборка
COPY src ./src
RUN gradle bootJar --no-daemon

# Этап запуска
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Создание пользователя (безопасность)
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Копирование собранного jar файла
COPY --from=build /app/build/libs/*.jar app.jar

# Настройка прав
RUN chown -R appuser:appgroup /app
USER appuser

# Порт приложения
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]