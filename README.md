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
y se registra cada compra exitosa mediante `model.RepositorioCompras`.

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
| Eventos disponibles | `Eventos.fxml` + `EventosController` | `eventos.jpg` |
| Compra de boletos | `Compra.fxml` + `CompraController` | `compra.jpg` |
| Historial de compras | `Historial.fxml` + `HistorialController` | `historial.jpg` |
| Panel de administración (Eventos, Usuarios y Reportes) | `PanelAdmin.fxml` + `PanelAdminController` | `panel-admin.jpg`, `panel-admin-usuarios.jpg`, `panel-admin-reportes.jpg` |

## Séptimo y octavo entregable

**Consolidación del Sprint 2 (Semanas 7 y 8): historias HU-05 a HU-09 integradas y control de versiones con Gitflow.**

- **Historias completadas** (planificación en [`docs/decisiones-sprint7-8.md`](./docs/decisiones-sprint7-8.md)):
  - **HU-05** (Xiomara): selección de método de pago desde la pantalla de compra.
  - **HU-06** (Xiomara): historial de compras del cliente con filtro por fechas.
  - **HU-07** (Daniel): gestión de eventos (crear, editar y eliminar) desde el
    panel de administración, con validaciones y confirmación.
  - **HU-08** (Daniel): gestión de usuarios (listado, activar y desactivar)
    desde el panel de administración.
  - **HU-09** (Daniel): reporte de ventas por categoría de evento, generado
    desde `CompraPersistencia.generarReportePorCategoria(...)` y accesible
    desde la pestaña de reportes del panel de administración.
- **Gitflow:** el trabajo se estructuró en tres tipos de ramas:
  - `main`: rama estable, solo recibe merges desde `develop` al cierre de cada
    sprint.
  - `develop`: rama de integración; todas las `feature/*` se fusionan aquí.
  - `feature/*`: una rama por historia o cambio puntual, con Pull Request
    dirigido a `develop` para revisión antes del merge.
- **Convenciones de commits:** todos los mensajes siguen
  [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`,
  `fix:`, `docs:`, `refactor:`, `chore:`), un cambio por commit y un archivo
  o cambio lógico por Pull Request cuando aplica.
- **Pull Requests y revisión:** cada `feature/*` se abrió como PR contra
  `develop`, con Eddie como revisor asignado según los roles de gestión
  ([`docs/decisiones-sprint7-8.md`](./docs/decisiones-sprint7-8.md)). El
  historial de PRs queda visible en la pestaña
  [Pull Requests](../../pulls?q=is%3Apr) del repositorio.

## Diagramas UML

Todos los diagramas se encuentran en la carpeta [`diagramasUML/`](./diagramasUML):

- [Diagrama de clases](./diagramasUML/diagrama-clases.jpg)
- [Diagrama de clases de la capa DAO](./diagramasUML/diagrama-clases-dao.jpeg)
- [Diagrama de clases de la interfaz gráfica](./diagramasUML/diagrama-clases-gui.jpg)
- [Diagrama de secuencia](./diagramasUML/diagrama-secuencia.jpg)
- [Diagrama de actividad](./diagramasUML/diagrama-actividad.jpg)

## Estructura del proyecto

```
Proyecto_Gestion_Boletos_POO/
├── build.gradle
├── settings.gradle
├── docker-compose.yml
├── diagramasUML/          Diagramas UML en JPG
├── wireframes/            Wireframes de la interfaz en JPG
├── docs/
│   └── decisiones-sprint7-8.md
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
        │   ├── hilos/             HiloMensaje
        │   ├── model/             Usuario, Administrador, Comprador,
        │   │                      CompradorVIP, Evento, Compra,
        │   │                      GestorUsuarios, HistorialCompras,
        │   │                      RepositorioCompras, SistemaGestionBoletos
        │   ├── patrones/
        │   │   └── strategy/      Descuento, DescuentoFijo, DescuentoPorcentaje
        │   ├── persistencia/      ConexionBD, ConfigBD, UsuarioPersistencia,
        │   │                      EventoPersistencia, CompraPersistencia,
        │   │                      RegistroCompra
        │   ├── ui/                ConsolaUI
        │   └── util/              Alertas, Navegacion
        └── resources/             Login, Registro, Eventos, Compra,
                                   Historial y PanelAdmin (.fxml),
                                   estilos.css
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

Para entrar como cliente, crea una cuenta desde el botón **Registrarme**.

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

## Sprint 2 (Semanas 7 y 8)

Este sprint cubre del 21 de septiembre al 4 de octubre. Las historias
pendientes son HU-05, HU-07 y HU-08, distribuidas entre Xiomara
(Compra/Reserva) y Daniel (Administración/Reportes). Eddie coordina el
seguimiento del tablero y la revisión de Pull Requests.

Ver el detalle de planificación, prioridades y riesgos en
[`docs/decisiones-sprint7-8.md`](./docs/decisiones-sprint7-8.md) y en el
tablero del proyecto en GitHub Projects.

**Estado al cierre de la semana 7:** HU-01 a HU-04, HU-06 y HU-09
completadas. Las pantallas de HU-05, HU-07 y HU-08 están implementadas en
JavaFX; su conexión con la base de datos se completa en la semana 8.