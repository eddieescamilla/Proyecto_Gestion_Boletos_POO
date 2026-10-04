# Sistema de Gestión de Venta de Boletos para Eventos

[![build (main)](https://github.com/eddieescamilla/Proyecto_Gestion_Boletos_POO/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/eddieescamilla/Proyecto_Gestion_Boletos_POO/actions/workflows/build.yml?query=branch%3Amain)
[![build (develop)](https://github.com/eddieescamilla/Proyecto_Gestion_Boletos_POO/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/eddieescamilla/Proyecto_Gestion_Boletos_POO/actions/workflows/build.yml?query=branch%3Adevelop)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![JavaFX 21](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-336791.svg)](https://www.postgresql.org/)

## Integrantes

| Nombre | Carnet | GitHub |
|---|---|---|
| José Edgardo Rosales Escamilla | 00129426 | [@eddieescamilla](https://github.com/eddieescamilla) |
| Xiomara Molina Amaya | 00220826 | [@Xio7w7](https://github.com/Xio7w7) |
| Daniel Eduardo García Hernández | 00253220 | [@TheSulak3](https://github.com/TheSulak3) |

## Descripción

Desarrollo de clases e implementación de patrones de diseño para estructurar y
gestionar el sistema de compra de boletos para eventos, promoviendo una
arquitectura modular y robusta que mejora la organización del sistema,
facilita su mantenimiento y optimiza la integración entre la lógica de
negocio, la interfaz gráfica y la persistencia de datos.

## Tercer entregable

Actualización de diagramas de clases y desarrollo en Java, integrando
**clases abstractas** e **interfaces** (contratos) al diseño:

- `Persona` se convierte en **clase abstracta**: modela el concepto común a
  `Comprador` y `CompradorVIP`, pero no puede instanciarse por sí sola.
- `Descuento` se convierte en **interfaz**: define el contrato
  `aplicar(double)` que implementan `DescuentoPorcentaje` y `DescuentoFijo`.
- `Compra` depende del contrato `Descuento`, no de una implementación
  concreta, aplicando polimorfismo.

**Patrón implementado:** Strategy, aplicado al cálculo de descuentos sobre el
total de una compra. Las clases del patrón (`Descuento`, `DescuentoFijo`,
`DescuentoPorcentaje`) se organizan en el paquete `patrones.strategy`,
separado del paquete `model`, que contiene la lógica de negocio principal.

## Cuarto entregable

Generación de diagramas UML: secuencia y actividad.

Se incorpora ejecución concurrente mediante dos hilos (`hilos.HiloMensaje`)
que envían mensajes de forma paralela al iniciar el sistema. La entrada de
datos por consola se centraliza en `ui.ConsolaUI`. Además, se agrega
autenticación real (`model.GestorUsuarios`, `model.Usuario`,
`model.Administrador`), se extiende `Evento` con categoría, fecha y lugar,
y se registra cada compra exitosa (la primera versión usó
`model.RepositorioCompras` como archivo plano, reemplazada en el quinto
entregable por `persistencia.CompraPersistencia` sobre PostgreSQL).

## Quinto entregable

Actualización de diagramas UML: estructurando la capa de persistencia de
datos utilizando patrón DAO y base de datos PostgreSQL (Docker).

Se separa la lógica de negocio de la persistencia: el paquete `dao` define
el contrato genérico (`DAO<T>`), y el paquete `persistencia` lo implementa
mediante JDBC contra PostgreSQL (`UsuarioPersistencia`, `EventoPersistencia`,
`CompraPersistencia`), con `ConexionBD`/`ConfigBD` gestionando la conexión.
Se implementan HU-06 (historial de compras del cliente, con filtro por
fecha) y HU-09 (reporte de ventas por categoría de evento).

## Sexto entregable

**Diseñar la GUI en JavaFX siguiendo los wireframes.**

- **Wireframes:** se diseñaron en Figma las 8 pantallas de la aplicación y se
  exportaron en JPG a la carpeta [`wireframes/`](./wireframes).
- **Interfaz gráfica en JavaFX 21:** cada pantalla es un archivo FXML en
  `src/main/resources`, vinculado a su controlador en el paquete
  `controladores`. Los colores de los wireframes se aplican con una hoja de
  estilos común (`estilos.css`).
- **Nueva estructura del proyecto:**
  - `diagramasUML/`: los diagramas UML se movieron desde `docs/` y se agregó
    el diagrama de clases de la interfaz gráfica.
  - `catalogo`: enumeraciones reutilizables (`Categoria`, `RolUsuario`,
    `TipoPago`).
  - `util`: plantillas de alertas (`Alertas`) y cambio de pantallas
    (`Navegacion`).
  - `MainFX`: clase principal para ejecutar la aplicación con la interfaz
    gráfica.
- **Flujo conectado:** el inicio de sesión valida las credenciales contra la
  base de datos y abre la pantalla correspondiente al rol del usuario
  (Eventos para clientes, Panel de Administración para administradores). La
  pantalla de Eventos carga los eventos reales desde PostgreSQL.
- **Estilo de código:** todo el código Java sigue **Google Java Style**.

| Pantalla | Archivos | Wireframe |
|---|---|---|
| Inicio de sesión | `Login.fxml` + `LoginController` | `login.jpg` |
| Registro | `Registro.fxml` + `RegistroController` | `registro.jpg` |
| Recuperar clave | `RecuperarClave.fxml` + `RecuperarClaveController` | — (agregada en 1.8.0) |
| Eventos disponibles | `Eventos.fxml` + `EventosController` | `eventos.jpg` |
| Compra de boletos | `Compra.fxml` + `CompraController` | `compra.jpg` |
| Historial de compras | `Historial.fxml` + `HistorialController` | `historial.jpg` |
| Panel de administración (Eventos, Usuarios y Reportes) | `PanelAdmin.fxml` + `PanelAdminController` | `panel-admin.jpg`, `panel-admin-usuarios.jpg`, `panel-admin-reportes.jpg` |

## Entregable final

**Entregable final: Persistencia de datos en la GUI de JavaFX con soporte de hilos.**

- **Persistencia desde la GUI con el patrón DAO:** los controladores FXML
  guardan, consultan, actualizan y eliminan datos a través de las clases del
  paquete `persistencia`, que implementan la interfaz `dao.DAO<T>`:
  - **Eventos** (Panel de Administración): agregar, editar, eliminar y listar.
  - **Usuarios:** registro, inicio de sesión, activar y desactivar cuentas.
  - **Compras:** confirmar la compra (con descuento y descuento del
    inventario), historial del cliente con filtro por fechas y reporte de
    ventas por categoría.
- **Soporte de hilos con `Task`:** toda operación que accede a la base de
  datos desde la GUI corre en segundo plano dentro de un `javafx.concurrent.Task`,
  ejecutado por la clase `hilos.EjecutorTareas` en un hilo demonio. La interfaz
  nunca se bloquea: el resultado se muestra en `setOnSucceeded` y los errores
  en `setOnFailed`, que JavaFX ejecuta en su propio hilo, así que la pantalla
  se actualiza de forma segura. Esto incluye el inicio de sesión y el registro
  de usuarios (que además cifran la contraseña con BCrypt), la carga de
  eventos, la compra, el historial y todas las acciones del panel.
- **Controladores y FXML actualizados:** los encabezados muestran el usuario
  en sesión (`util.Sesion`), el administrador no puede desactivar su propia
  cuenta y no se pueden eliminar eventos que ya tienen compras.
- **Documentación Javadoc:** generada con `./gradlew javadoc` en la carpeta
  [`documentacion/`](./documentacion) (abrir `documentacion/index.html`).
- **Diagramas UML actualizados** en [`diagramasUML/`](./diagramasUML): clases
  de la interfaz gráfica e hilos, y clases de la persistencia con DAO.
- **Base de datos:** el esquema se crea y actualiza con migraciones de Flyway
  (`src/main/resources/db/migration`), con integridad referencial entre
  compras, usuarios y eventos.

> **Sobre la persistencia en archivos planos:** desde el quinto entregable el
> equipo decidió guardar los datos en PostgreSQL (Docker) en lugar de archivos
> de texto, para tener integridad referencial, consultas por fecha y categoría,
> y descuento de inventario sin sobreventa. Gracias al patrón DAO, los
> controladores no dependen del tipo de almacenamiento: cambiarlo solo implica
> otra implementación de `DAO<T>`.

## Sprint 2 (semanas 7 y 8): historias de usuario y Gitflow

- **Historias completadas** (planificación en
  [`docs/decisiones-sprint7-8.md`](./docs/decisiones-sprint7-8.md)):
  - **HU-05** (Xiomara): selección de método de pago en la pantalla de compra.
  - **HU-06** (Xiomara): historial de compras del cliente con filtro por fechas.
  - **HU-07** (Daniel): gestión de eventos (crear, editar y eliminar).
  - **HU-08** (Daniel): gestión de usuarios (listado, activar y desactivar).
  - **HU-09** (Daniel): reporte de ventas por categoría de evento.
- **Gitflow:** `main` contiene las versiones entregadas, `develop` integra el
  trabajo del equipo y cada cambio se hace en una rama `feature/*`, `fix/*` o
  `docs/*` que se integra a `develop` mediante Pull Request. Durante el sprint,
  tres Pull Requests (#41, #44 y #45) se integraron por error directamente en
  `main` (la base por defecto de GitHub); se corrigió sincronizando `develop`
  y, al cierre, se activó la protección de `main`: solo acepta cambios por
  Pull Request y con el build de GitHub Actions en verde.
- **Convenciones de commits:**
  [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`,
  `fix:`, `docs:`, `refactor:`, `chore:`, `style:`, `test:`, `ci:`).
- **Integración continua:** GitHub Actions compila el proyecto y corre las
  pruebas en cada Pull Request.

## Diagramas UML

Todos los diagramas se encuentran en la carpeta [`diagramasUML/`](./diagramasUML):

- [Diagrama de clases](./diagramasUML/diagrama-clases.jpg)
- [Diagrama de clases de la persistencia (DAO, PostgreSQL y Flyway)](./diagramasUML/diagrama-clases-dao.jpg)
- [Diagrama de clases de la interfaz gráfica, controladores e hilos](./diagramasUML/diagrama-clases-gui.jpg)
- [Diagrama de secuencia](./diagramasUML/diagrama-secuencia.jpg)
- [Diagrama de actividad](./diagramasUML/diagrama-actividad.jpg)

## Documentación

- [**Documentación Javadoc**](./documentacion/index.html) — referencia de
  todas las clases del proyecto, generada con `./gradlew javadoc` en la
  carpeta [`documentacion/`](./documentacion).

La documentación complementaria vive en la carpeta [`docs/`](./docs):

- [**Manual de usuario**](./docs/manual-usuario.md) — cómo usar la app
  desde la GUI, paso a paso (cliente y administrador).
- [**Manual técnico**](./docs/manual-tecnico.md) — vista de alto nivel
  de la arquitectura, capas, patrones y flujo de datos.
- [**Troubleshooting**](./docs/troubleshooting.md) — errores comunes
  y cómo resolverlos.
- [**Modelo entidad-relación**](./docs/entidad-relacion.md) — esquema
  completo de la base de datos.
- [**ADRs (decisiones arquitectónicas)**](./docs/adr/README.md) —
  bitácora de por qué se tomó cada decisión estructural.
- [**Empaquetado con jlink**](./docs/empaquetado-jlink.md) — guía para
  generar la imagen de runtime.
- [**Decisiones del Sprint 2**](./docs/decisiones-sprint7-8.md) —
  planificación, roles y riesgos del sprint 2.

## Estructura del proyecto

```
Proyecto_Gestion_Boletos_POO/
├── build.gradle
├── settings.gradle
├── docker-compose.yml
├── diagramasUML/          Diagramas UML en JPG
├── wireframes/            Wireframes de la interfaz en JPG
├── documentacion/         Documentación generada con Javadoc
├── docs/                  Manuales, ADRs y decisiones del sprint
└── src/
    └── main/
        ├── java/
        │   ├── Main.java          Aplicación por consola
        │   ├── MainFX.java        Aplicación con interfaz gráfica
        │   ├── catalogo/          Categoria, RolUsuario, TipoPago
        │   ├── controladores/     LoginController, RegistroController,
        │   │                      EventosController, CompraController,
        │   │                      HistorialController, PanelAdminController
        │   ├── dao/               DAO
        │   ├── hilos/             EjecutorTareas, HiloMensaje
        │   ├── model/             Usuario, Administrador, Comprador,
        │   │                      CompradorVIP, Evento, Compra, Asiento,
        │   │                      DescuentoConfig, GestorUsuarios,
        │   │                      HistorialCompras, SistemaGestionBoletos
        │   ├── patrones/
        │   │   └── strategy/      Descuento, DescuentoFijo, DescuentoPorcentaje
        │   ├── persistencia/      ConexionBD, ConfigBD, UsuarioPersistencia,
        │   │                      EventoPersistencia, CompraPersistencia,
        │   │                      DescuentoPersistencia, AuditoriaPersistencia,
        │   │                      AsientoPersistencia, RegistroCompra
        │   ├── ui/                ConsolaUI
        │   └── util/              Alertas, Navegacion, Sesion, PasswordHasher
        └── resources/             Login, Registro, Eventos, Compra,
                                   Historial y PanelAdmin (.fxml),
                                   estilos.css, logback.xml y
                                   db/migration (Flyway)
```

> **Nota sobre la persistencia:** desde el quinto entregable, el paquete
> `persistencia` guarda los datos en PostgreSQL (Docker) mediante el patrón
> DAO, en lugar de archivos planos.

## Funcionalidad principal

- Registro e inicio de sesión de usuarios, con roles de cliente y
  administrador.
- Consulta de eventos disponibles y compra de boletos, validando el stock.
- Comprador **VIP** (`CompradorVIP`), con 10% de descuento adicional.
- Códigos de descuento: `DESC10` (10%) y `DESC5` (5%).
- Historial de compras del cliente, con filtro por fechas.
- Reporte de ventas por categoría de evento (administrador).

## Cómo ejecutar

### Requisitos

- JDK 21 o superior
- Docker Desktop (para la base de datos PostgreSQL)
- IntelliJ IDEA (recomendado)

### 1. Levantar la base de datos

Con Docker Desktop abierto, desde la raíz del proyecto:

```
docker compose up -d
```

La primera vez se crean las tablas y se cargan los datos iniciales
automáticamente.

### 2. Ejecutar la interfaz gráfica

```
.\gradlew run
```

En Mac o Linux: `./gradlew run`. Gradle descarga JavaFX automáticamente según
el sistema operativo.

**Usuario administrador de prueba:** `admin@boletos.com` / `admin123`

> Desde la 1.8.0 el administrador se siembra solo si la variable de entorno
> `BOLETOS_ADMIN_PASSWORD` está definida (en el sistema o en el archivo
> `.env`). El `.env.example` ya la trae con `admin123` para desarrollo local;
> para producción déjala en blanco o quítala para que la app arranque sin
> credenciales conocidas, y crea el admin con un script propio.

Para entrar como cliente, crea una cuenta desde el botón **Registrarme**.
Si olvidas tu clave, usa el botón **¿Olvidaste tu clave?** del login para
restablecerla ingresando tu correo y una clave nueva.

### 3. Ejecutar la versión por consola (opcional)

Desde IntelliJ IDEA, ejecutar la clase `Main.java`.

## Metodología de trabajo

Este proyecto se desarrolla siguiendo **Gitflow** como metodología de trabajo
colaborativo:

- `main`: versión entregada de cada semana.
- `develop`: integración del trabajo del equipo.
- `feature/*` y `docs/*`: una rama por tarea, integrada a `develop` mediante
  Pull Request.

Los mensajes de commit siguen el formato de Conventional Commits (`feat:`,
`docs:`, `style:`, `refactor:`, `chore:`, `build:`).
