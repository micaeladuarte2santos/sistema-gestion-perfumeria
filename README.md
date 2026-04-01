# Sistema de Gestión - Perfumería

Guía para levantar el proyecto localmente.

Requisitos:
- Java 17+
- MySQL
- Spring Boot (aplicación construida con Spring Boot)

Pasos:

## 1. Crear la base de datos

Abre tu cliente MySQL y ejecuta:

```sql
CREATE DATABASE perfumeria_db;
```

## 2. Revisar application.properties

Archivo: `src/main/resources/application.properties`

Revisa `src/main/resources/application.properties` y ajusta las credenciales si es necesario. Por defecto el proyecto usa:

```
spring.datasource.url=jdbc:mysql://localhost:3306/perfumeria_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

## 3. Levantar/Ejecutar la aplicación


## 4. Ejecutar el script de datos (INSERTs)

El script con los INSERTs se llama `insert-Perfumeria.sql`.

Una vez la aplicación esté corriendo, ejecuta el script SQL con los INSERTs a la base de datos.
