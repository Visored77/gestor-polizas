# Gestor de Pólizas

API REST para la gestión de pólizas de arrendamiento, construida con Spring Boot y MySQL.

## Tecnologías

- Java 21
- Spring Boot 4.1.1 (Web, Data JPA, Validation)
- MySQL 8
- Lombok
- Maven (con wrapper)

## Modelo de dominio

- **Póliza** (`poliza`): datos del tomador, asegurado, beneficiario, vigencia y valores económicos.
- **Riesgo** (`riesgo`): inmuebles y arrendatarios amparados por una póliza.
- **Auditoría Core** (`auditoria_core`): registro de sincronización con el CORE legado.

## Requisitos previos

- JDK 21 o superior (incluye `javac`)
- Una instancia de MySQL con la base de datos `gestion_polizas`

### Levantar MySQL con Podman/Docker

```bash
podman run -d --name mi-mysql \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=gestion_polizas \
  -p 3306:3306 \
  -v mysql-data:/var/lib/mysql \
  docker.io/library/mysql:8
```

El esquema (tablas `poliza`, `riesgo`, `auditoria_core`) debe existir en la base `gestion_polizas`.

## Configuración

La conexión se define en `src/main/resources/application.properties`. Ajusta usuario,
contraseña y URL según tu entorno.

## Ejecutar

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Endpoints principales

| Método | Ruta                              | Descripción                        |
|--------|-----------------------------------|------------------------------------|
| GET    | `/api/polizas`                    | Listar pólizas                     |
| GET    | `/api/polizas/{id}`               | Obtener una póliza                 |
| POST   | `/api/polizas`                    | Crear póliza                       |
| PUT    | `/api/polizas/{id}`               | Actualizar póliza                  |
| DELETE | `/api/polizas/{id}`               | Eliminar póliza                    |
| GET    | `/api/riesgos`                    | Listar riesgos                     |
| GET    | `/api/riesgos/poliza/{polizaId}`  | Listar riesgos de una póliza       |
| POST   | `/api/riesgos/poliza/{polizaId}`  | Crear riesgo asociado a una póliza |
| GET    | `/api/auditorias`                 | Listar registros de auditoría      |
| POST   | `/api/auditorias`                 | Crear registro de auditoría        |

## Compilar

```bash
./mvnw clean package
```
