# Usar una imagen base con OpenJDK y Debian Slim
FROM openjdk:17-jdk-slim

# Instalar Nginx
RUN apt-get update && apt-get install -y nginx

# Eliminar la configuración predeterminada de Nginx
RUN rm -f /etc/nginx/sites-enabled/default

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR de la aplicación
COPY target/*.jar app.jar

# Copiar la configuración de Nginx
COPY default.conf /etc/nginx/conf.d/default.conf

# Copiar el script de entrada
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Exponer el puerto 80
EXPOSE 80

# Iniciar el script de entrada
CMD ["/entrypoint.sh"]
