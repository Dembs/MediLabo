FROM eclipse-temurin:17-jdk-alpine
# Définir des arguments pour faciliter la maintenance
ARG JAR_FILE=target/api_gateway-0.0.1-SNAPSHOT.jar
# Définir le répertoire de travail dans le conteneur
WORKDIR /app
# Copier le fichier .jar packagé dans le répertoire de travail du conteneur
# Renommer en app.jar pour simplifier la commande ENTRYPOINT
COPY ${JAR_FILE} app.jar
EXPOSE 8080
# Exécuter l'application lorsque le conteneur démarre
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
