# Historial de cambios

Todas las versiones publicadas del proyecto se listan aquí, en orden inverso.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y
la numeración de versiones respeta [Semantic Versioning](https://semver.org/).

## [No liberado]

Cambios en `develop` que aún no han sido promovidos a `main`.

- (nada por ahora)

## [1.4.0] - 2026-09-29

Cierre del Sprint 2. Corresponde a la Entrega 4 del curso.

### Agregado

- **Panel de administración conectado a la base de datos** — agregar, editar
  y eliminar eventos (HU-07); activar y desactivar usuarios (HU-08); generar
  reporte de ventas por categoría (HU-09). Todas las operaciones de
  persistencia se ejecutan en `Task` de JavaFX para no bloquear la interfaz.
- Métodos `EventoPersistencia.actualizarEvento(Evento)` y
  `UsuarioPersistencia.actualizarEstado(String, boolean)` en la capa DAO.
- Configuración de credenciales por archivo `.env` local (además de las
  variables de entorno ya soportadas), con `.env.example` como plantilla.
- Workflow de GitHub Actions (`.github/workflows/build.yml`) que compila y
  corre las pruebas contra un servicio de Postgres 16 en cada push y PR.
- Pruebas unitarias para `Categoria`, `DescuentoFijo`/`DescuentoPorcentaje`,
  `Evento` y `Compra`.
- Tabla de integrantes en el README con nombre, carnet y usuario de GitHub.

### Cambiado

- El constructor de `Evento` ya no rechaza fechas pasadas. La validación de
  fecha futura al crear queda a cargo del formulario del Panel de
  Administración; el constructor permite rehidratar eventos históricos desde
  la base de datos.
- `Compra.aplicarDescuento(String)` acepta códigos `null` y normaliza el
  código con `trim().toUpperCase()` antes de compararlo.

## [1.3.0] - 2026-09-28

Cierre del Sprint 1 (Semanas 7 y 8). Corresponde a la Entrega 3 del curso.

### Agregado

- Interfaz gráfica en JavaFX 21 con las 8 pantallas del sistema
  (Login, Registro, Eventos, Compra, Historial, Panel de Administración
  con sus tres pestañas), enlazadas por FXML a controladores en el paquete
  `controladores/`.
- Paquete `catalogo/` con las enumeraciones `Categoria`, `RolUsuario` y
  `TipoPago`.
- Paquete `util/` con `Alertas` y `Navegacion`.
- Persistencia con PostgreSQL vía JDBC en el paquete `persistencia/`
  (`ConexionBD`, `ConfigBD`, `UsuarioPersistencia`, `EventoPersistencia`,
  `CompraPersistencia`) y contrato genérico `DAO<T>`.
- Historial de compras del cliente con filtro por fechas (HU-06).
- Reporte de ventas por categoría de evento (HU-09).
- Javadoc en todas las clases y miembros públicos.
- Documento `docs/decisiones-sprint7-8.md` con la planificación del sprint.

## [1.2.0] - Semana 5

- Ejecución concurrente con dos hilos en `hilos/HiloMensaje`.
- Autenticación real con `model.GestorUsuarios` y `model.Usuario`.
- `Evento` extendido con categoría, fecha y lugar.
- Registro de compras exitosas en `model.RepositorioCompras`.

## [1.1.0] - Semana 4

- Patrón Strategy aplicado a los descuentos (`patrones.strategy.Descuento`
  con implementaciones `DescuentoFijo` y `DescuentoPorcentaje`).

## [1.0.0] - Semana 3

- Diseño e implementación de clases abstractas (`Persona`) e interfaces
  (`Descuento`) para la primera versión del modelo de dominio.
- Diagrama de clases inicial.
