# Decisiones de gestión — Sprint 2 (Semanas 7 y 8)

## Roles
- **Eddie** — Coordinador + Documentación. Con su módulo de Autenticación (HU-01, HU-02) completado, se traslada a coordinación, revisión de Pull Requests y QA para este sprint.
- **Xiomara** — Diseño + Compra/Reserva. Implementa HU-05 y HU-06; mantiene actualizado el diagrama UML.
- **Daniel** — Administración/Reportes + Control de versiones. Implementa HU-07 a HU-09; gestiona ramas y orden de merge.

## Planificación
Sprint 2 cubre las semanas 7 y 8 (21 de septiembre – 4 de octubre), como un solo sprint de dos semanas, consistente con la cadencia adoptada desde el Sprint 1.

| Historia | Responsable | Prioridad | Inicio | Fin | Dependencia |
|---|---|---|---|---|---|
| HU-05: Seleccionar método de pago | Xiomara | Media | 21 sep | 24 sep | Ninguna |
| HU-07: Gestionar eventos | Daniel | Alta | 21 sep | 24 sep | Ninguna |
| HU-08: Gestionar usuarios | Daniel | Media | 25 sep | 28 sep | Depende de HU-07 |
| HU-06: Ver historial de compras | Xiomara | Baja | 28 sep | 1 oct | Depende de HU-05 |
| HU-09: Generar reportes | Daniel | Baja | 29 sep | 2 oct | Depende de HU-08 |

**Hito 1 (27 sep):** HU-05 y HU-07 completadas y en revisión.
**Hito 2 (4 oct):** HU-05 a HU-09 integradas a develop/main, documentación cerrada.

## Riesgos
Registrados y priorizados en el tablero de GitHub Projects (ver Issues vinculados) y en el informe de Entrega 3. Los de mayor prioridad son: conflictos de merge en semana 8, concentración de carga de trabajo en Daniel, y la dependencia funcional HU-05 → HU-06.

## Justificación de prioridades
La prioridad de cada historia se definió según qué tan bloqueante es para el resto del sistema si se retrasa, no de forma arbitraria: HU-07 es Alta porque es prerrequisito de contenido para otras historias; HU-05 y HU-08 son Media porque son necesarias pero no detienen el resto si se atrasan ligeramente; HU-06 y HU-09 son Baja porque son funciones de consulta sin impacto bloqueante.
