FROM eclipse-temurin:17 AS build
WORKDIR /app
COPY . .

# ✅ Añadir esta línea para dar permisos de ejecución al wrapper
RUN chmod +x mvnw

# Ejecutar Maven wrapper para generar el .jar
RUN ./mvnw clean package -DskipTests

# ========================
# FASE DE EJECUCIÓN
# ========================
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]