# ADR 0002 — Persistencia con PostgreSQL y patrón DAO

- Estado: aceptada
- Fecha: 2026-09-14
- Contexto: Sprint 1, Semana 6 del curso.

## Contexto

La indicación oficial del curso pide implementar el patrón DAO
mapeado a archivos planos (paquetes `dao/` y `persistencia/`). El
ejemplo oficial del ATM usa SQL Server vía JDBC.

Como equipo, buscamos una implementación que:

- Cumpla la rúbrica del curso (DAO gestionando persistencia de datos).
- Sea suficientemente cercana a un entorno real como para ser útil
  después del curso.
- No dependa de motores con licenciamiento restrictivo.

## Decisión

Implementar la capa `persistencia/` con **PostgreSQL** corriendo en
Docker local, y JDBC como driver. Se conservan los paquetes `dao/`
(contrato genérico `DAO<T>`) y `persistencia/` (implementaciones)
tal como pide la indicación.

Las credenciales se leen desde variables de entorno o desde un
archivo `.env` local (ver ADR 0003 pendiente).

## Consecuencias

**A favor**:

- La rúbrica evalúa que el patrón DAO gestione correctamente
  guardar/acceder/modificar datos, sin exigir un mecanismo
  específico. La implementación cumple la letra y el espíritu.
- PostgreSQL es open source, sin licenciamiento restrictivo.
- JDBC estándar con `PreparedStatement` mantiene el código portable
  entre motores: solo cambia la cadena de conexión y algún detalle
  menor de sintaxis en `CREATE TABLE` (por ejemplo `SERIAL`).
- Docker Compose garantiza que todos los colaboradores levanten la
  misma versión de la base.

**En contra**:

- Nueva dependencia externa (Docker Desktop) para desarrollo local.
- Un colaborador sin Docker no puede correr la app.

## Alternativas consideradas

- **Archivos planos** (opción del enunciado): descartada porque no
  representaría los flujos reales que enfrentarán los desarrolladores
  después del curso.
- **SQLite embebido**: se consideró como paso intermedio. Se descartó
  porque agregar y luego migrar a PostgreSQL era doble esfuerzo.
- **SQL Server**: opción del material oficial. Descartada por su
  licenciamiento restrictivo y porque el equipo no tiene infraestructura
  para hospedarlo.
