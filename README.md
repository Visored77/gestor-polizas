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
- MySQL 8 (local, Docker o Podman)

## Montar la base de datos

### 1. Levantar MySQL con Podman/Docker

```bash
podman run -d --name mi-mysql \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=gestion_polizas \
  -p 3306:3306 \
  -v mysql-data:/var/lib/mysql \
  docker.io/library/mysql:8
```

(Con Docker, reemplaza `podman` por `docker`.)

### 2. Crear el esquema (tablas `poliza` y `riesgo`)

El DDL está en [`src/main/resources/schema.sql`](src/main/resources/schema.sql). Ejecútalo:

```bash
# opcion A: desde el contenedor
podman exec -i mi-mysql mysql -uroot -proot123 < src/main/resources/schema.sql

# opcion B: con un cliente MySQL local
mysql -h 127.0.0.1 -P 3306 -uroot -proot123 < src/main/resources/schema.sql
```

El script crea la base `gestion_polizas` (si no existe) y las tablas `poliza` y
`riesgo` con su relación (una póliza tiene uno o varios riesgos).

También puedes ejecutarlo desde un cliente gráfico (MySQL Workbench, DBeaver)
abriendo el archivo `schema.sql` y corriéndolo.

### 3. Cargar datos de ejemplo (opcional)

Para poblar la base con datos de prueba (10 pólizas individuales y 5 colectivas
con 3, 4, 5, 6 y 7 riesgos), ejecuta
[`src/main/resources/data.sql`](src/main/resources/data.sql):

```bash
podman exec -i mi-mysql mysql -uroot -proot123 < src/main/resources/data.sql
```

> Nota: `data.sql` borra los datos existentes de `poliza` y `riesgo` antes de insertar.

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
