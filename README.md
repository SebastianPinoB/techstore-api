# TechStore API

Backend desarrollado con Spring Boot para la gestión de productos de la plataforma TechStore.

## Descripción

TechStore API es un microservicio REST desarrollado en Java 21 y Spring Boot que permite realizar operaciones CRUD sobre productos. La aplicación se encuentra containerizada mediante Docker y desplegada en AWS utilizando Docker Swarm, GitHub Actions y Amazon ECR.

---

# Arquitectura de la Solución

La solución está compuesta por los siguientes componentes:

- Spring Boot API
- PostgreSQL
- Docker
- Docker Swarm
- GitHub Actions (CI/CD)
- Amazon ECR
- AWS EC2
- Función Serverless (FaaS)

Arquitectura general:

Cliente → API REST (Spring Boot)
↓
Docker Container
↓
Docker Swarm
↓
AWS EC2
↓
PostgreSQL

GitHub
↓
GitHub Actions
↓
Amazon ECR
↓
Docker Swarm

---

# Tecnologías Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Maven
- Docker
- Docker Compose
- Docker Swarm
- GitHub Actions
- AWS EC2
- AWS ECR

---

# Dockerfile

El proyecto utiliza un Dockerfile Multi-Stage para optimizar el tamaño de la imagen y mejorar la seguridad.

Características:

- Construcción mediante Maven dentro del contenedor.
- Imagen final basada en Eclipse Temurin JRE Alpine.
- Reducción del tamaño de la imagen.
- Separación entre etapa de compilación y ejecución.

Construcción local:

```bash
docker build -t techstore-api .
```

Ejecución:

```bash
docker run -p 8080:8080 techstore-api
```

---

# Docker Compose

El proyecto incluye un archivo docker-compose.yml que permite desplegar:

- PostgreSQL
- API Spring Boot

Ejecución:

```bash
docker compose up -d
```

---

# Integración Continua y Despliegue Continuo (CI/CD)

Se implementó un pipeline utilizando GitHub Actions.

Ubicación:

```text
.github/workflows/ci-cd.yml
```

Etapas:

## Build

- Descarga del código
- Configuración de Java 21
- Compilación mediante Maven

## Test

- Ejecución de validaciones durante la construcción

## Provisionamiento Cloud

- Verificación automática del repositorio ECR
- Creación automática si no existe

## Despliegue

- Construcción de imagen Docker
- Publicación automática en Amazon ECR

---

# Amazon ECR

Las imágenes Docker son almacenadas en Amazon Elastic Container Registry (ECR).

Repositorio:

```text
techstore-api
```

Tags generados:

```text
latest
github.sha
```

---

# Docker Swarm

Se configuró un clúster Docker Swarm compuesto por:

## Manager

Responsable de:

- Administración del clúster
- Programación de servicios
- Gestión de réplicas

## Worker

Responsable de:

- Ejecución de contenedores
- Participación en servicios distribuidos

Inicialización:

```bash
docker swarm init
```

Agregar Worker:

```bash
docker swarm join --token <TOKEN> <IP_MANAGER>:2377
```

Verificar nodos:

```bash
docker node ls
```

Desplegar stack:

```bash
docker stack deploy -c docker-compose.yml techstore
```

Ver servicios:

```bash
docker service ls
```

---

# Configuración de Variables de Entorno

La aplicación utiliza variables de entorno para la conexión a PostgreSQL.

Variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SERVER_PORT
```

Configuración en application.properties:

```properties
server.port=${SERVER_PORT:8080}

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/techstore}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:admin123}
```

---

# Función Serverless (FaaS)

El proyecto incluye una función serverless desarrollada en Java.

Ubicación:

```text
lambda/
```

Funcionalidad:

- Recepción de precio y stock
- Cálculo automático de IVA
- Cálculo de valor total del inventario

Ejemplo de entrada:

```json
{
  "precio": 10000,
  "stock": 5
}
```

Ejemplo de salida:

```json
{
  "precio": 10000,
  "stock": 5,
  "iva": 1900,
  "valorTotalStock": 59500
}
```

---

# Escalabilidad

La aplicación fue diseñada para ejecutarse sobre Docker Swarm utilizando múltiples réplicas.

Configuración:

```yaml
deploy:
  replicas: 2
```

Beneficios:

- Alta disponibilidad
- Distribución de carga
- Recuperación automática ante fallos

---

# Evidencias de Despliegue

Se realizaron pruebas exitosas de:

- Construcción de imágenes Docker
- Publicación en Amazon ECR
- Despliegue en Docker Swarm
- Ejecución de servicios distribuidos
- Consumo de endpoints mediante Postman

---

# Limitaciones del Entorno AWS Academy

Durante el desarrollo se intentó implementar servicios administrados adicionales como AWS RDS y AWS Lambda.

La cuenta AWS Academy utilizada presentaba restricciones IAM que impedían:

- rds:CreateDBInstance
- iam:CreateRole
- iam:PassRole

Por esta razón:

- PostgreSQL fue desplegado dentro del clúster Docker Swarm.
- Se entrega el código fuente completo de la función serverless para su despliegue en un entorno AWS sin restricciones.

---

# Autor

Sebastián Pino

Proyecto desarrollado para la asignatura de Arquitectura Cloud y Microservicios.