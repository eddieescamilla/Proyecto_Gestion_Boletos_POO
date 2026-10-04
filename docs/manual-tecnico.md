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
| `model/` | Clases del dominio: `Usuario`, `Administrador`, `Comprador`, `CompradorVIP`, `Evento`, `Compra`, `Asiento`, `DescuentoConfig`, `GestorUsuarios`, `HistorialCompras`, `SistemaGestionBoletos`. |
| `patrones/strategy/` | Patrón Strategy para descuentos: `Descuento`, `DescuentoFijo`, `DescuentoPorcentaje`. |
| `persistencia/` | Implementaciones JDBC de los DAO: `UsuarioPersistencia`, `EventoPersistencia`, `CompraPersistencia`, `DescuentoPersistencia`, `AuditoriaPersistencia`, `AsientoPersistencia`; conexión y configuración: `ConexionBD`, `ConfigBD`. |
| `ui/` | Utilidades de entrada por consola (`ConsolaUI`). |
| `util/` | Utilidades de GUI: `Alertas` (plantillas de alerts), `Navegacion` (cambio de pantallas), `Sesion` (usuario autenticado), `PasswordHasher` (BCrypt). |

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

### Singleton (implícito) + pool de conexiones

`ConexionBD.obtenerDataSource()` mantiene un único pool de conexiones
HikariCP durante la vida del proceso (desde 1.8.0; antes era una
`Connection` singleton). Cada DAO pide una conexión por operación con
`try-with-resources` y el pool las reparte entre los hilos de JavaFX
que corren en segundo plano.

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

- `usuarios` (correo PK, nombre, clave hash BCrypt, rol, activo)
- `eventos` (nombre_evento PK, categoria, fecha, lugar,
  inventario_disponible, precio_boleto)
- `compras` (id PK, correo_comprador, nombre_evento,
  categoria_evento, cantidad_boletos, total, fecha)
- `descuento_config` (codigo PK, tipo, valor, activo) — códigos de
  descuento configurables desde base de datos (desde 1.6.0).
- `auditoria` (id PK, actor, accion, detalle, fecha) — bitácora de
  acciones del Panel de Administración (desde 1.7.0).
- `asientos` (nombre_evento + numero_asiento PK compuesta, vendido)
  — data layer para asientos numerados (desde 1.8.0, fase 1 del #60).

Las foreign keys y los índices están cubiertos por las migraciones
V3 y V6 de Flyway en `src/main/resources/db/migration`.

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

Las pruebas de integración de la capa DAO viven en
`src/test/java/persistencia/*IT.java` (`UsuarioPersistenciaIT`,
`DescuentoPersistenciaIT`, `CompraPersistenciaIT`) y están gateadas
por la variable `BOLETOS_INTEGRATION_TESTS=1` que CI setea
automáticamente contra un servicio de Postgres 16. En local, se
corren con `BOLETOS_INTEGRATION_TESTS=1 ./gradlew test` con el
contenedor de Docker arriba.

## Observabilidad

El proyecto usa SLF4J + Logback. Configuración en
`src/main/resources/logback.xml`:

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
