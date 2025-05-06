FROM eclipse-temurin:17-jdk-alpine
# Définie des arguments pour faciliter la maintenance
ARG JAR_FILE=target/front_end-0.0.1-SNAPSHOT.jar
# Définie le répertoire de travail dans le conteneur
WORKDIR /app
# Copie le fichier .jar packagé dans le répertoire de travail du conteneur
# Renomme en app.jar pour simplifier la commande ENTRYPOINT
COPY ${JAR_FILE} app.jar
EXPOSE 8082
# Exécute l'application lorsque le conteneur démarre
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
