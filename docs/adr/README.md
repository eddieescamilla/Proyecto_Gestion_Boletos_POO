# Registro de decisiones arquitectónicas (ADR)

Este directorio contiene las decisiones arquitectónicas del proyecto,
siguiendo el formato de
[ADR](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions).

Cada archivo describe una decisión concreta: el contexto que la
motivó, la decisión tomada, sus consecuencias y las alternativas
consideradas.

## Índice

- [0001 — Usar Flyway para migraciones de esquema](./0001-usar-flyway-para-migraciones.md) *(propuesta — PR #62)*
- [0002 — Persistencia con PostgreSQL y patrón DAO](./0002-persistencia-con-postgresql-y-dao.md)
- [0003 — Task de JavaFX para operaciones de persistencia](./0003-task-para-operaciones-de-persistencia-en-javafx.md)

## Cómo agregar una decisión nueva

1. Copiar el ADR más reciente como plantilla.
2. Numerarlo con el siguiente entero disponible.
3. Escribir el contexto que motiva la decisión, la decisión en sí,
   sus consecuencias (a favor y en contra) y las alternativas
   consideradas.
4. Estado inicial: `propuesta`. Al aprobarse en el equipo pasa a
   `aceptada`. Si más adelante se revierte, pasa a `superada` y se
   enlaza la ADR que la reemplaza.
5. Agregarla a este índice.
