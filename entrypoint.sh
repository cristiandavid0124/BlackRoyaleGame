#!/bin/sh

# Iniciar la aplicación Spring Boot en segundo plano
java -jar /app/app.jar --server.address=0.0.0.0 &

# Iniciar Nginx en primer plano
nginx -g 'daemon off;'
