# ADR 0003 — Task de JavaFX para operaciones de persistencia

- Estado: aceptada
- Fecha: 2026-09-28
- Contexto: Sprint 2, Semana 8. Cierre del Panel de Administración.

## Contexto

El material de Semana 8 exige, para las aplicaciones JavaFX con
persistencia, "uso correcto de `Task` y `Platform.runLater()` sin
bloquear la interfaz". El modelo mental es el *Single-Threaded Model*
de JavaFX: solo el *JavaFX Application Thread* puede tocar el Scene
Graph, y cualquier operación pesada debe delegarse a un hilo de fondo.

Nuestra primera versión del Panel de Administración ejecutaba las
llamadas al DAO directamente en el hilo de UI. Con Postgres local no
se notaba lag, pero técnicamente bloqueaba la interfaz y no cumplía
la rúbrica al pie de la letra.

## Decisión

Envolver todas las operaciones de base de datos del
`PanelAdminController` en instancias de `javafx.concurrent.Task<T>`,
disparadas en un `Thread` demonio con un helper privado
(`ejecutarEnSegundoPlano(Task<?>)`). Las actualizaciones a la
interfaz se hacen en los handlers `setOnSucceeded` y `setOnFailed`,
que corren automáticamente en el *JavaFX Application Thread*.

Para actualizaciones intermedias desde dentro del `call()` (por
ejemplo el mensaje "Generando reporte..." en HU-09) se usa
`Platform.runLater()`.

## Consecuencias

**A favor**:

- Cumple la rúbrica de Semana 8 sin ambigüedad.
- La interfaz nunca se congela mientras la BD responde,
  independientemente del tamaño del resultado.
- El patrón es documentado y estándar en JavaFX, fácil de replicar en
  otros controladores.

**En contra**:

- Más código por cada handler (Task + setOnSucceeded + setOnFailed
  vs. una sola llamada síncrona).
- La lógica queda en 3 lugares (validación en el hilo de UI, IO en
  `call()`, actualización de UI en los handlers). Requiere disciplina
  para mantenerlo consistente.

## Alternativas consideradas

- **Dejarlo síncrono**: descartada por la rúbrica, aunque
  funcionalmente andaba con Docker local.
- **`Service` en lugar de `Task`**: `Service` es útil para tareas
  reejecutables. Los handlers del Panel de Administración son
  disparos únicos, así que `Task` alcanza.
- **CompletableFuture / async manual**: descartada porque
  `Task`/`Platform.runLater()` es la API idiomática de JavaFX y la
  que espera la rúbrica.
