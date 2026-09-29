# ADR 0001 — Usar Flyway para migraciones de esquema

- Estado: aceptada
- Fecha: 2026-09-28
- Contexto: Sprint 2, endurecimiento de la capa de persistencia.

## Contexto

Durante los primeros sprints el esquema se creaba manualmente desde
`ConexionBD.crearTablas()` con sentencias `CREATE TABLE IF NOT EXISTS`.
El método hacía dos cosas mezcladas: aplicar el esquema y sembrar datos
iniciales. Esto trajo varios problemas:

- No había forma de agregar columnas o restricciones sin borrar la base
  de datos entera.
- Los cambios en el esquema convivían con los cambios en el código,
  pero no quedaba un historial explícito de qué migró y cuándo.
- Cada quien tenía que recordar aplicar cambios manuales en su copia
  local.

## Decisión

Se adopta [Flyway](https://flywaydb.org/) como herramienta de
migraciones. El esquema y los datos semilla viven en
`src/main/resources/db/migration/` como archivos versionados
(`V1__esquema_inicial.sql`, `V2__datos_iniciales.sql`,
`V3__integridad_referencial_e_indices.sql`).

`ConexionBD.obtenerConexion()` corre `Flyway.migrate()` antes de
devolver la conexión. Se usa `baselineOnMigrate=true` para que las
bases de datos que ya existían adopten V1 como línea base sin perder
datos.

## Consecuencias

**A favor**:

- Historial explícito del esquema, versionado con Git.
- Cada colaborador ejecuta las mismas migraciones automáticamente al
  arrancar la app.
- El pipeline de CI parte de una base limpia y aplica las migraciones
  en orden.
- Fácil auditar cuándo y por qué se agregó una columna o restricción.

**En contra**:

- Nueva dependencia (`org.flywaydb:flyway-core` y el plugin de Gradle).
- Curva de aprendizaje pequeña para el equipo, que debe aprender el
  convenio de nombres (`V<n>__<descripcion>.sql`).
- Los cambios ad-hoc directamente en la base de datos quedan fuera del
  historial y pueden generar conflictos.

## Alternativas consideradas

- **Mantener `CREATE TABLE IF NOT EXISTS`**: descartada por los
  problemas mencionados arriba.
- **Liquibase**: opción equivalente, más pesada. Flyway es más simple
  y suficiente para el tamaño del proyecto.
- **Migraciones manuales fuera de la app**: descartada porque quería
  que la app pudiera arrancar en limpio en cualquier entorno sin
  intervención manual.
