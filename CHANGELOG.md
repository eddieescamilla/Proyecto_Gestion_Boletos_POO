# Historial de cambios

Todas las versiones publicadas del proyecto se listan aquí, en orden inverso.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y
la numeración de versiones respeta [Semantic Versioning](https://semver.org/).

## [No liberado]

Cambios en `develop` que aún no han sido promovidos a `main`.

- (nada por ahora)

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
