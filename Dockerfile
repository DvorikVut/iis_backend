# Используем официальный образ OpenJDK 17 в качестве базового
FROM maven:3.9.9-eclipse-temurin-22-jammy AS build

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Copy the pom.xml and source code to the container
COPY pom.xml .
COPY src ./src

# Build the application with Maven
RUN mvn clean install -DskipTests

# Копируем файл jar в контейнер
COPY target/IIS-v1.jar /app/IIS.jar

# Указываем команду для запуска Spring Boot приложения
CMD ["java", "-jar", "IIS.jar"]

# Открываем порт приложения
EXPOSE 8080