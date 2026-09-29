# Guía de contribución

Este documento resume cómo trabajar sobre el repositorio: cómo levantar el
entorno, cómo nombrar ramas, cómo escribir commits y cómo abrir Pull
Requests. Aplica a los tres integrantes del equipo y a cualquier
colaborador externo autorizado.

## Requisitos

- **JDK 21** o superior. Se recomienda Temurin.
- **Docker Desktop** para levantar la base de datos.
- **Git** con acceso al repositorio.
- **IntelliJ IDEA** recomendado como IDE (Community o Ultimate).

## Levantar el entorno local

1. Clonar el repositorio.
2. Copiar `.env.example` a `.env` y ajustar credenciales si es necesario.
3. Levantar PostgreSQL con Docker:

   ```
   docker compose up -d
   ```

4. Ejecutar la aplicación:

   ```
   .\gradlew run     # Windows
   ./gradlew run     # Linux/macOS
   ```

   Gradle descarga JavaFX automáticamente según el sistema operativo.

5. Iniciar sesión con el administrador de prueba:
   `admin@boletos.com` / `admin123`.

## Estilo de código

Todo el código Java sigue **Google Java Style Guide**:

- Indentación de 2 espacios.
- Llaves en la misma línea que la declaración.
- `PascalCase` para clases e interfaces, `lowerCamelCase` para métodos y
  variables, `UPPER_SNAKE_CASE` para constantes, `lowercase` para paquetes.
- Todo miembro `public` debe llevar Javadoc.
- Preferir composición sobre herencia cuando aplique.

## Flujo de trabajo — Gitflow

El repositorio sigue Gitflow con tres tipos de ramas persistentes y
ramas efímeras por tarea:

- **`main`**: rama estable. Solo recibe merges desde `develop` al cierre
  de cada sprint (release).
- **`develop`**: rama de integración. Todas las ramas de trabajo se
  fusionan aquí.
- **`feature/*`**, **`fix/*`**, **`chore/*`**, **`docs/*`**, **`refactor/*`**,
  **`test/*`**, **`ci/*`**: una rama por tarea. Se cierra por Pull Request
  contra `develop`.

### Nombres de rama

Prefijo por tipo de cambio, seguido de un slug corto y descriptivo:

```
feature/panel-admin-persistencia
fix/evento-constructor-fecha
chore/db-flyway-hardening
docs/readme-usuarios-github
test/pruebas-unitarias-modelo
ci/github-actions-build
```

## Convención de commits

Los mensajes de commit siguen
[Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>(<alcance opcional>): <resumen en imperativo>

<cuerpo opcional explicando el "por qué">
```

Tipos aceptados: `feat`, `fix`, `refactor`, `chore`, `docs`, `test`,
`ci`, `style`, `perf`, `build`.

Ejemplos:

```
feat(gui): conectar Panel de Administracion con la persistencia
fix(model): permitir rehidratar eventos con fecha pasada
chore(db): agregar foreign keys e indices al schema
docs(readme): agregar usuarios de github a la lista de integrantes
test: pruebas unitarias para modelo, estrategias y catalogo
```

Reglas adicionales:

- Un cambio por commit siempre que sea posible.
- El resumen va en imperativo y en minúscula.
- No más de 72 caracteres en la primera línea.
- El cuerpo debe explicar el "por qué" del cambio, no el "qué" (eso lo
  muestra el diff).

## Pull Requests

- Se abren siempre contra `develop`, nunca contra `main`.
- Un cambio lógico por PR.
- Título en minúscula siguiendo la convención de commits.
- Descripción con secciones `Cambios`, `Motivacion` y `Como probar`
  (existe una plantilla que se llena automáticamente al abrir el PR).
- Revisor asignado: **@eddieescamilla** para PRs de todo el equipo.
- No mergear sin al menos una revisión aprobada.
- El CI (workflow `build`) debe pasar antes de mergear.

## Issues

- Etiquetas mínimas: `bug`, `feature`, `docs`, `chore`, `seguridad`.
- Se abren con las plantillas de `.github/ISSUE_TEMPLATE/`.
- Los issues de backlog fuera de un sprint se marcan con `backlog`.

## Releases

Al cierre de cada sprint:

1. Se abre un PR de `develop` → `main` con título
   `release: promover develop a main (cierre Sprint N)`.
2. La descripción resume los PRs incluidos.
3. Después del merge, se actualiza `CHANGELOG.md` con la versión
   correspondiente.
