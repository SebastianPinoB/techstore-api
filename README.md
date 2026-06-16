# TechStore API - Arquitectura Distribuida y Despliegue en la Nube

## Descripcion
Microservicios RESTful para gestion de catalogo. Java 21, Spring Boot, PostgreSQL, JWT.

### Requisitos

Java 21, Maven 3.9+, PostgreSQL 15 instalados.
base de datos `techstore` creada en PostgreSQL

Base de datos: `techstore`
Puerto: `5432`
usuario: `postgres`
contraseña: `admin123`


Este proyecto consiste en el backend de una solución empresarial distribuida para la gestión de productos tecnológicos (*TechStore*). La aplicación está construida utilizando **Spring Boot**, persiste sus datos en **PostgreSQL**, e integra un flujo arquitectónico desacoplado y asíncrono para la sincronización de inventario mediante tecnologías Serverless (FaaS).

---

## 🚀 Arquitectura y Flujo en la Nube (AWS)

El ciclo de vida del microservicio está completamente automatizado e integrado con servicios Cloud:

1. **Código Fuente:** Gestionado en GitHub.
2. **Integración Continua (CI):** Automatizada mediante GitHub Actions.
3. **Registro de Contenedores:** Almacenamiento centralizado de imágenes en **AWS ECR (Elastic Container Registry)**.

---

## 🤖 1. Pipeline de CI/CD y Gestión de Imágenes Cloud (IE4)

El repositorio incluye un flujo de automatización configurado en `.github/workflows/ci-cd.yml` que garantiza que cada versión del código se empaquete y asegure directamente en el entorno de AWS.

### Flujo Automático del Pipeline:
* **Compilación y Test:** El runner descarga el repositorio, configura Java 17 y compila el proyecto con Maven.
* **Autenticación Cloud:** Inicia sesión de forma segura en AWS usando credenciales temporales de AWS Academy (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`).
* **Despliegue en AWS ECR:** Construye la imagen Docker del microservicio y la sube de forma exitosa al registro remoto bajo el tag `techstore-api:latest`.

> 🌐 **Identificador del Registro en AWS:** `989804099124.dkr.ecr.***.amazonaws.com`

---

## 📦 2. Ejecución de la Imagen desde AWS ECR (IE1)

El archivo `Dockerfile` del repositorio está diseñado para ser agnóstico al entorno, permitiendo que la imagen alojada en AWS ECR pueda ser desplegada y ejecutada de manera reproducible en cualquier orquestador Cloud (como AWS ECS o App Runner) o clúster distribuido.

### Comando para desplegar la imagen directo desde ECR:
```bash
# 1. Autenticar el cliente Docker local con el registro remoto de AWS
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 989804099124.dkr.ecr.us-east-1.amazonaws.com

# 2. Descargar y ejecutar la imagen compilada por el pipeline
docker run -d -p 8080:8080 --name techstore-api [989804099124.dkr.ecr.us-east-1.amazonaws.com/techstore-api:latest](https://989804099124.dkr.ecr.us-east-1.amazonaws.com/techstore-api:latest)