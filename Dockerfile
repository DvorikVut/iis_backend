# Используем официальный образ OpenJDK 17 в качестве базового
FROM maven:3.9.9-eclipse-temurin-22-jammy AS build

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Copy the pom.xml and source code to the container
COPY pom.xml .
COPY src ./src

# Build the application with Maven
RUN mvn clean package

FROM openjdk:22

# Копируем файл jar в контейнер
COPY --from=build /app/target/IIS-v1.jar .

# Указываем команду для запуска Spring Boot приложения
CMD ["java", "-jar", "IIS-v1.jar"]

# Открываем порт приложения
EXPOSE 8080
