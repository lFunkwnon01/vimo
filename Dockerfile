# Utiliza una imagen oficial de OpenJDK para ejecutar aplicaciones Java
FROM openjdk:17-jdk-alpine

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el jar generado al contenedor
COPY target/proyecto-0.0.1-SNAPSHOT.jar /app/app.jar

# Expone el puerto en el que se ejecuta la aplicación
EXPOSE 8096

# Define el punto de entrada para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]