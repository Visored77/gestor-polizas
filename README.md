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

Reglas de negocio:

- La prima se calcula automáticamente: `canon_mensual * meses_vigencia`.
- Una póliza individual tiene un único riesgo; una colectiva puede tener varios.
- Solo las pólizas colectivas admiten agregar riesgos.
- No se puede renovar una póliza cancelada.
- Al cancelar una póliza se cancelan en cascada todos sus riesgos.
- La renovación incrementa el canon según el IPC y recalcula la prima; el estado pasa a `RENOVADA`.

## Requisitos previos

- JDK 21 o superior (incluye `javac`)
- Una instancia de MySQL con la base de datos `gestion_polizas` y las tablas `poliza` y `riesgo`

### Levantar MySQL con Podman/Docker

```bash
podman run -d --name mi-mysql \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=gestion_polizas \
  -p 3306:3306 \
  -v mysql-data:/var/lib/mysql \
  docker.io/library/mysql:8
```

## Configuración

La conexión se define en `src/main/resources/application.properties`. Ajusta usuario,
contraseña y URL según tu entorno. La clave del mock del CORE se configura con `api.key`.

## Ejecutar

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

## Endpoints

| Método | Ruta                              | Descripción                                   |
|--------|-----------------------------------|-----------------------------------------------|
| GET    | `/polizas`                        | Listar pólizas (filtros `?tipo=` y `?estado=`)|
| GET    | `/polizas/{id}`                   | Obtener una póliza                            |
| POST   | `/polizas`                        | Crear póliza                                  |
| GET    | `/polizas/{id}/riesgos`           | Listar riesgos de una póliza                  |
| POST   | `/polizas/{id}/riesgos`           | Agregar riesgo (solo pólizas colectivas)      |
| POST   | `/polizas/{id}/renovar`           | Renovar póliza (ajuste por IPC)               |
| POST   | `/polizas/{id}/cancelar`          | Cancelar póliza (cascada a sus riesgos)       |
| POST   | `/riesgos/{id}/cancelar`          | Cancelar un riesgo                            |
| POST   | `/core-mock/evento`               | Mock del CORE: registra el evento en logs     |

### Seguridad

El endpoint del mock del CORE (`/core-mock/**`) exige el header `x-api-key` con la clave
configurada (`api.key`, por defecto `123456`). El resto de endpoints no la requieren.

## Probar con Postman

En `postman/Gestor-Polizas.postman_collection.json` hay una colección lista para importar
con todos los endpoints.

## Compilar y probar

```bash
./mvnw clean package   # compila, ejecuta tests y genera el jar
./mvnw test            # solo tests
```
