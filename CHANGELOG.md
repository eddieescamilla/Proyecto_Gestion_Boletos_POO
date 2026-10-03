# Historial de cambios

Todas las versiones publicadas del proyecto se listan aquí, en orden inverso.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y
la numeración de versiones respeta [Semantic Versioning](https://semver.org/).

## [No liberado]

Cambios en `develop` que aún no han sido promovidos a `main`.

- (nada por ahora)

## [1.8.0] - 2026-10-03

Release grande post-cierre que resuelve los findings pendientes de la
auditoría red/blue team, cosecha la primera tanda de bumps de Dependabot
e inaugura features del backlog (recuperación de clave MVP y data layer
de asientos numerados).

### Agregado

- **Pantalla de recuperación de clave** (PR #106, cierra #56). Nuevo botón
  **¿Olvidaste tu clave?** en `Login.fxml` que abre `RecuperarClave.fxml`;
  el usuario ingresa correo + clave nueva + confirmación, el sistema
  verifica que la cuenta exista y actualiza la clave con BCrypt. Es un
  MVP: la UI advierte que no hay verificación por correo/SMS; el flujo
  completo con token y envío out-of-band queda para una iteración futura.
- **Data layer para asientos numerados** (PR #107, fase 1 de #60).
  Migración `V6__asientos.sql` con tabla `asientos` (PK compuesta
  `nombre_evento + numero_asiento`, FK con cascada e índice sobre
  disponibles), seed vía `generate_series` que pre-popula cada evento con
  tantos asientos como `inventario_disponible`. Nuevos
  `model.Asiento` y `persistencia.AsientoPersistencia` con
  `listarPorEvento`, `listarDisponibles`, `marcarVendido` (UPDATE
  condicional atómico) y `contarDisponibles`. Falta la UI (seat picker)
  y el cableo del flujo de compra para la siguiente fase.
- **Gate del admin seed** (PR #104, cierra #77 y el finding P2 CWE-798
  del scan externo). Nueva migración `V5__gate_admin_seed.sql` que borra
  el admin plano que sembraba V2 cuando sigue teniendo la clave
  original. `ConexionBD.sembrarAdminSiCorresponde()` corre al terminar
  Flyway y, si la variable `BOLETOS_ADMIN_PASSWORD` está definida (en el
  sistema o en el `.env`), hace un `INSERT ... ON CONFLICT DO NOTHING`
  con BCrypt del hash; si no está definida, no siembra. Correo
  configurable con `BOLETOS_ADMIN_EMAIL` (default `admin@boletos.com`).
  Nota en el README y en `docs/manual-usuario.md`.

### Cambiado

- **Pool de conexiones HikariCP** (PR #105, cierra #99 y el finding medio
  CWE-662 improper synchronization). Antes todos los DAO compartían una
  única `Connection` estática expuesta por
  `ConexionBD.obtenerConexion()`. Con las pantallas corriendo en
  segundo plano con `javafx.concurrent.Task`, dos tareas concurrentes
  podían terminar ejecutando SQL sobre la misma `Connection`, que la
  spec de JDBC no garantiza thread-safe. `ConexionBD` ahora expone un
  `javax.sql.DataSource` (HikariCP, `maximumPoolSize=10`,
  `minimumIdle=2`, `connectionTimeout=10s`); cada DAO
  (`UsuarioPersistencia`, `EventoPersistencia`, `CompraPersistencia`,
  `DescuentoPersistencia`, `AuditoriaPersistencia`,
  `AsientoPersistencia`) pide una conexión por operación en
  `try-with-resources`.
- **Gradle wrapper** 9.6.0 → 9.8.0 (PR #87).
- **PostgreSQL JDBC** 42.7.4 → 42.7.13 (PR #85).
- **SLF4J API** 2.0.13 → 2.0.20 (PR #88).
- **Flyway** 10.20.1 → 13.8.1 (PR #86, plugin + flyway-core +
  flyway-database-postgresql). Salto mayor; los tests de integración
  contra Postgres 16 pasaron sin cambios en las migraciones V1-V4.
- **GitHub Actions**: `actions/checkout` v4 → v7 (PR #91),
  `actions/setup-java` v4 → v6 (PR #92), `github/codeql-action` v3 →
  v4 (PR #90).

## [1.7.1] - 2026-10-03

Patch de pulido post-1.7.0. Cierra el seguimiento manual que había quedado
abierto en code scanning y endurece la UI de login contra doble clic.

### Corregido

- **Doble clic en Login y Registro** (PR #97). Los handlers `iniciarSesion`
  y `crearCuenta` ahora reciben el `ActionEvent` y deshabilitan el botón
  mientras la tarea de fondo está en vuelo. Antes era posible disparar dos
  intentos en paralelo mientras BCrypt verificaba, con el riesgo de dos
  conexiones JDBC concurrentes y alertas duplicadas.
- **Alertas abiertas de CodeQL** (PR #98). Elimina la variable local
  `compraFinal` que nunca se leía en `CompraController`
  (java/local-variable-is-never-read, CWE-561) y centraliza el parseo de
  enteros de la consola en un helper que atrapa `NumberFormatException` y
  devuelve `-1` en vez de propagar (java/uncaught-number-format-exception
  sobre `ConsolaUI.leerOpcionInicio`, `leerOpcion` y `leerCantidadBoletos`,
  CWE-248).

## [1.7.0] - 2026-10-03

Release de auditoría post-cierre. Consolida housekeeping, observabilidad,
hardening de gobernanza, cobertura de capa DAO y migración del esquema a
Flyway.

### Agregado

- **Manuales del repositorio** (PR #78) recuperados de `docs/documentacion-completa`
  que no habían entrado al merge original: `docs/manual-usuario.md`,
  `docs/manual-tecnico.md` y `docs/troubleshooting.md`, enlazados desde el
  README en una sección nueva "Documentación".
- **Gobernanza de seguridad** (PR #80): `SECURITY.md` con política de
  reporte, `CODE_OF_CONDUCT.md` basado en Contributor Covenant 2.1,
  `.github/dependabot.yml` para updates semanales de Gradle, GitHub
  Actions y Docker, y `.github/workflows/codeql.yml` para análisis SAST
  sobre Java/Kotlin en cada push, PR y una vez por semana.
- **Pruebas de integración de la capa DAO** (PR #81): `UsuarioPersistenciaIT`,
  `DescuentoPersistenciaIT` y `CompraPersistenciaIT`, gateadas por la
  variable `BOLETOS_INTEGRATION_TESTS=1` que CI setea automáticamente.
- **Observabilidad con SLF4J + Logback** (PR #64): `logback.xml` con
  rotación diaria (14 días, 200 MB total). `ConexionBD` loguea los
  eventos de conexión y migración.
- **Capa de auditoría** (PR #64): nueva tabla `auditoria`,
  `persistencia.AuditoriaPersistencia` y cableo en `PanelAdminController`
  para dejar constancia de agregar/editar/eliminar eventos,
  activar/desactivar usuarios y generar reportes, con el correo del
  usuario en sesión como actor.
- **Migración del esquema a Flyway** (PR #62): migraciones
  `V1__esquema_inicial.sql`, `V2__datos_iniciales.sql`,
  `V3__integridad_referencial_e_indices.sql` y
  `V4__descuento_config_y_auditoria.sql`. `ConexionBD` ahora delega la
  creación y poblamiento del esquema a Flyway con `baselineOnMigrate` para
  no romper instalaciones ya existentes. V3 agrega llaves foráneas desde
  `compras` hacia `usuarios` y `eventos`, e índices sobre las columnas
  más consultadas.

### Cambiado

- `build.gradle`: `group` pasa de `org.example` a `sv.edu.uca.poo.boletos`
  y `version` ahora sigue el `CHANGELOG` (`1.7.0`).

### Eliminado

- `model/RepositorioCompras` (PR #79). Era el escritor a archivo plano del
  tercer entregable, reemplazado en el quinto por
  `persistencia.CompraPersistencia`. Sin callers vivos desde entonces.

### Mantenimiento

- Normalización de estilo e indentación de `DescuentoPersistencia` y
  `DescuentoConfig` a 2 espacios (PR #79), más Javadoc completo que
  faltaba en el record de configuración.

## [1.6.0] - 2026-10-03

Cierre efectivo del Sprint 2. Consolida la lógica de negocio pendiente de
HU-05 y HU-06, la configurabilidad de descuentos, el endurecimiento de
seguridad del scan externo y los tres manuales que faltaban del repo.

### Agregado

- **Descuentos configurables desde base de datos** (PR #70). Nueva tabla
  `descuento_config`, DAO `DescuentoPersistencia` y seed inicial con `DESC10`
  (10%) y `DESC5` (5%). `Compra.aplicarDescuento(String)` ahora consulta la
  base y arma la estrategia correspondiente en vez de un `switch` hardcodeado.
- **Compra atómica** (PR #70). `EventoPersistencia.descontarInventarioAtomico`
  reemplaza el check + update separados por un único
  `UPDATE ... WHERE inventario_disponible >= ?`, evitando sobreventa bajo
  compras concurrentes.
- **Cableado real de HU-05 (compra) y HU-06 (historial)** (PR #71) sobre la
  API nueva de #70. Nuevo `util.Sesion` que mantiene el usuario autenticado;
  todo el I/O corre dentro de `javafx.concurrent.Task`.
- **Hash BCrypt para contraseñas** (PR #75). Dependencia nueva
  `at.favre.lib:bcrypt:0.10.2`, `util.PasswordHasher` y migración en caliente
  de cualquier fila heredada en texto plano al primer login exitoso. El
  admin se siembra ya hasheado.
- **Correo normalizado y comparación exacta en historial** (PR #75). El
  correo se guarda en minúsculas en registro y login, y
  `HistorialCompras.buscarPorCliente` cambia `equalsIgnoreCase` por `equals`
  exacto para cerrar la fuga por variante de capitalización.
- **PostgreSQL publicado solo en loopback** (PR #75). `docker-compose.yml`
  publica el puerto 5432 únicamente en `127.0.0.1`.
- **Manuales nuevos** (recuperados de `docs/documentacion-completa`):
  `docs/manual-usuario.md`, `docs/manual-tecnico.md` y
  `docs/troubleshooting.md`, enlazados en el README.

### Corregido

- **Bug SQL en `DescuentoPersistencia.buscarPorId`** (PR #73). La columna
  `descuento_config.activo` está definida como `INTEGER` en el schema pero
  la consulta usaba `activo = TRUE`, lo que hacía que Postgres rechazara el
  SELECT con `operator does not exist: integer = boolean`. El resultado era
  que aplicar cualquier código válido desde la GUI lanzaba una
  `RuntimeException`.

## [1.5.0] - 2026-09-29

Segundo release del Sprint 2 (Semanas 7 y 8). Consolida el trabajo de
endurecimiento y calidad que quedó fuera del corte inicial de la Entrega 4.

### Agregado

- Configuración de credenciales por archivo `.env` local (además de las
  variables de entorno ya soportadas), con `.env.example` como plantilla.
- Workflow de GitHub Actions (`.github/workflows/build.yml`) que compila y
  corre las pruebas contra un servicio de Postgres 16 en cada push y PR.
- Pruebas unitarias para `Categoria`, `DescuentoFijo`/`DescuentoPorcentaje`,
  `Evento` y `Compra`.
- Tabla de integrantes en el README con nombre, carnet y usuario de GitHub.

## [1.4.0] - 2026-09-28

Primer release del Sprint 2 (Semanas 7 y 8). Corresponde a la **Entrega 4**
del curso.

### Agregado

- **Panel de administración conectado a la base de datos** — agregar, editar
  y eliminar eventos (HU-07); activar y desactivar usuarios (HU-08); generar
  reporte de ventas por categoría (HU-09). Todas las operaciones de
  persistencia se ejecutan en `Task` de JavaFX para no bloquear la interfaz.
- Métodos `EventoPersistencia.actualizarEvento(Evento)` y
  `UsuarioPersistencia.actualizarEstado(String, boolean)` en la capa DAO.

### Cambiado

- El constructor de `Evento` ya no rechaza fechas pasadas. La validación de
  fecha futura al crear queda a cargo del formulario del Panel de
  Administración; el constructor permite rehidratar eventos históricos desde
  la base de datos.
- `Compra.aplicarDescuento(String)` acepta códigos `null` y normaliza el
  código con `trim().toUpperCase()` antes de compararlo.

## [1.3.0] - 2026-09-19

Semana 6. Introducción de la capa de persistencia.

### Agregado

- Persistencia con PostgreSQL vía JDBC en el paquete `persistencia/`
  (`ConexionBD`, `ConfigBD`, `UsuarioPersistencia`, `EventoPersistencia`,
  `CompraPersistencia`) y contrato genérico `DAO<T>`.
- `docker-compose.yml` con PostgreSQL 16 para desarrollo local.
- Historial de compras del cliente con filtro por fechas (HU-06).
- Reporte de ventas por categoría de evento (HU-09), en su versión de
  consola.

## [1.2.0] - 2026-09-12

Semana 5. Concurrencia y autenticación real.

### Agregado

- Ejecución concurrente con dos hilos en `hilos/HiloMensaje`.
- Autenticación real con `model.GestorUsuarios` y `model.Usuario`.
- `Evento` extendido con categoría, fecha y lugar.
- Registro de compras exitosas en `model.RepositorioCompras`.

## [1.1.0] - 2026-09-05

Semana 4. Patrón de diseño para descuentos.

### Agregado

- Patrón Strategy aplicado a los descuentos (`patrones.strategy.Descuento`
  con implementaciones `DescuentoFijo` y `DescuentoPorcentaje`).

## [1.0.0] - 2026-08-24

Semanas 1 a 3. Modelo inicial del dominio.

### Agregado

- Diseño e implementación de clases abstractas (`Persona`) e interfaces
  (`Descuento`) para la primera versión del modelo de dominio.
- Diagrama de clases inicial.
