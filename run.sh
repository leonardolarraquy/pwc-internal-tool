#!/bin/bash

# Script para construir el frontend, copiarlo a recursos estáticos y ejecutar el backend

set -e  # Detener si hay algún error

# Obtener el directorio del script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "Building frontend..."
cd frontend
npm run build
cd ..

echo "Copying frontend build to static resources..."
# Crear el directorio si no existe
mkdir -p src/main/resources/static
# Copiar los archivos
cp -r frontend/dist/* src/main/resources/static/

echo "Building and running backend..."
mvn clean compile spring-boot:run
