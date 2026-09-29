# Manual técnico

Vista de alto nivel de la arquitectura, las capas, el flujo de datos
y las decisiones estructurales del proyecto. Sirve como referencia
para colaboradores nuevos o para quien tenga que hacer mantenimiento
después del curso.

## Arquitectura en capas

El proyecto se organiza en tres capas separadas por responsabilidad:

```
┌─────────────────────────────────────────────────────┐
│  Capa de presentación (UI)                          │
│  ─ Vistas FXML en src/main/resources/               │
│  ─ Controladores en src/main/java/controladores/    │
└──────────────────┬──────────────────────────────────┘
                   │  Task de JavaFX + Platform.runLater
┌──────────────────▼──────────────────────────────────┐
│  Capa de negocio                                    │
│  ─ Modelo del dominio en src/main/java/model/       │
│  ─ Enums y utilidades en catalogo/ y util/          │
│  ─ Patrones de diseño en patrones/                  │
└──────────────────┬──────────────────────────────────┘
                   │  DAO<T> (interfaz genérica)
┌──────────────────▼──────────────────────────────────┐
│  Capa de persistencia                               │
│  ─ dao/DAO.java (contrato genérico)                 │
│  ─ persistencia/*.java (implementaciones JDBC)      │
│  ─ ConexionBD + ConfigBD                            │
└──────────────────┬──────────────────────────────────┘
                   │  JDBC (PreparedStatement)
                   ▼
             PostgreSQL 16 (Docker)
```

La comunicación entre capas es unidireccional: la presentación
llama al modelo, el modelo llama al DAO, el DAO habla con la base.
Ninguna capa inferior conoce a la superior.

## Estructura de paquetes

| Paquete | Responsabilidad |
|---|---|
| `Main` | Punto de entrada de la aplicación por consola. |
| `MainFX` | Punto de entrada de la aplicación con GUI (JavaFX). |
| `catalogo/` | Enums reutilizables: `Categoria`, `RolUsuario`, `TipoPago`. |
| `controladores/` | Controladores JavaFX enlazados a los FXML. |
| `dao/` | Contrato genérico `DAO<T>` con las operaciones CRUD comunes. |
| `hilos/` | Hilos concurrentes (`HiloMensaje`). |
| `model/` | Clases del dominio: `Usuario`, `Administrador`, `Comprador`, `CompradorVIP`, `Evento`, `Compra`, `GestorUsuarios`, `HistorialCompras`, `RepositorioCompras`, `SistemaGestionBoletos`. |
| `patrones/strategy/` | Patrón Strategy para descuentos: `Descuento`, `DescuentoFijo`, `DescuentoPorcentaje`. |
| `persistencia/` | Implementaciones JDBC de los DAO: `UsuarioPersistencia`, `EventoPersistencia`, `CompraPersistencia`; conexión y configuración: `ConexionBD`, `ConfigBD`. |
| `ui/` | Utilidades de entrada por consola (`ConsolaUI`). |
| `util/` | Utilidades de GUI: `Alertas` (plantillas de alerts), `Navegacion` (cambio de pantallas). |

## Patrones de diseño aplicados

### DAO (Data Access Object)

Aísla la lógica de acceso a datos del resto del sistema.
El contrato genérico `dao.DAO<T>` define las operaciones básicas
(`guardar`, `buscarPorId`, `listarTodos`, `eliminar`) y cada tipo
tiene su implementación concreta en `persistencia/`. Ver
[ADR 0002](./adr/0002-persistencia-con-postgresql-y-dao.md).

### Strategy

Los descuentos son intercambiables sin tocar la lógica de compra.
`patrones.strategy.Descuento` es la interfaz común y
`DescuentoFijo` y `DescuentoPorcentaje` son las estrategias
concretas. `Compra.aplicarDescuento(Descuento)` recibe cualquier
estrategia sin conocer su implementación.

### Singleton (implícito)

`ConexionBD.obtenerConexion()` mantiene una única instancia de la
conexión JDBC durante la vida del proceso. No es un Singleton puro
(la clase no bloquea instanciación) pero cumple el mismo objetivo.

## Flujo de datos típico — Compra de un boleto

```
Cliente hace clic en "Confirmar compra"
    ↓
CompraController.confirmarCompra() (JavaFX Application Thread)
    ↓
Valida los datos del formulario
    ↓
Envuelve la operación en un Task<T>
    ↓
Task.call() (hilo demonio)
    ↓
Compra.confirmarPago() ← model
    ↓
Evento.actualizarInventario(cantidad) ← model (descuenta stock)
    ↓
CompraPersistencia.guardarCompra(compra) ← persistencia
    ↓
UPDATE eventos SET inventario_disponible ... via JDBC
INSERT INTO compras (...) via JDBC
    ↓
Task.setOnSucceeded() (JavaFX Application Thread)
    ↓
Alertas.mostrarInformacion("Compra registrada")
```

Cada operación pesada (BD, IO) ocurre en un hilo de fondo. La
interfaz solo se actualiza en `setOnSucceeded`/`setOnFailed`, que
corren en el *JavaFX Application Thread* automáticamente. Ver
[ADR 0003](./adr/0003-task-para-operaciones-de-persistencia-en-javafx.md).

## Modelo de datos

El esquema completo está documentado en
[`entidad-relacion.md`](./entidad-relacion.md). Resumen:

- `usuarios` (correo PK, nombre, clave, rol, activo)
- `eventos` (nombre_evento PK, categoria, fecha, lugar,
  inventario_disponible, precio_boleto)
- `compras` (id PK, correo_comprador, nombre_evento,
  categoria_evento, cantidad_boletos, total, fecha)

Foreign keys, índices y una tabla `auditoria` adicional entran con
los PRs #62 y #64 (pendientes de revisión al momento de escribir
esto).

## Concurrencia

- **`hilos.HiloMensaje`**: hilo demostrativo que arranca en
  paralelo al `main` para cumplir el requerimiento de concurrencia
  del curso (Semana 5).
- **`javafx.concurrent.Task`**: patrón usado en el
  `PanelAdminController` para no bloquear la interfaz durante
  operaciones de base de datos.
- **`Platform.runLater(...)`**: se usa para actualizar la interfaz
  desde dentro del `call()` de un `Task` (por ejemplo, mostrar
  "Generando reporte..." antes de la consulta).

## Configuración

Las credenciales de la base de datos se leen de dos formas, en este
orden:

1. Archivo `.env` en la raíz del proyecto (opcional). Ver
   `.env.example` como plantilla.
2. Variables de entorno del sistema (`BOLETOS_DB_URL`,
   `BOLETOS_DB_USER`, `BOLETOS_DB_PASSWORD`).

El proyecto **nunca** hardcodea credenciales en el código. Ver
`persistencia.ConfigBD` para la lógica.

## Ejecución

- **Consola**: `Main` — para escenarios de prueba y para el
  requerimiento de concurrencia inicial.
- **GUI**: `MainFX` — punto de entrada real, arranca la aplicación
  JavaFX. El wrapper `.\gradlew run` lo invoca.

## Testing

Las pruebas unitarias viven en `src/test/java/` y usan JUnit 5.
Cubren `catalogo.Categoria`, las estrategias de descuento, `Evento`
(reglas de inventario y disponibilidad) y `Compra` (cálculo,
descuentos y confirmación).

No hay pruebas de integración con base de datos todavía. Se
podrían agregar con Testcontainers para levantar un Postgres real
en cada corrida del CI.

## Observabilidad

Con PR #64 mergeado (pendiente), el proyecto usa SLF4J + Logback.
Configuración en `src/main/resources/logback.xml`:

- Consola: nivel `INFO` para paquetes propios, `WARN` en el resto.
- Archivo: `logs/boletos.log` con rotación diaria, 14 días de
  historial, 200 MB total.

Además una tabla `auditoria` guarda las acciones sensibles del
Panel de Administración (agregar, editar, eliminar eventos;
activar y desactivar usuarios; generar reportes).

## Documentos relacionados

- [ADRs](./adr/README.md) — decisiones arquitectónicas
- [Entidad-Relación](./entidad-relacion.md) — esquema completo
- [Empaquetado con jlink](./empaquetado-jlink.md) — deployment
- [Manual de usuario](./manual-usuario.md) — cómo usar la app
- [Troubleshooting](./troubleshooting.md) — errores comunes
