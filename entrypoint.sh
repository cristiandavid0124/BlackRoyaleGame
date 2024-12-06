#!/bin/sh

# Verificar si MONGO_URI está configurado
if [ -z "$MONGO_URI" ]; then
  echo "ERROR: La variable de entorno MONGO_URI no está configurada."
  exit 1
fi

# Iniciar la aplicación Spring Boot en segundo plano
echo "Iniciando la aplicación Spring Boot..."
java -jar /app/app.jar --server.address=0.0.0.0 &

# Iniciar Nginx en primer plano
echo "Iniciando Nginx..."
nginx -g 'daemon off;'
