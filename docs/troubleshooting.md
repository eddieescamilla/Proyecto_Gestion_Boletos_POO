# Guía de troubleshooting

Errores comunes al levantar o correr la aplicación, con la causa
concreta y cómo resolverlos.

## Al levantar la aplicación

### `ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH`

Aparece al correr `.\gradlew run` en Windows sin el JDK configurado.

**Solución** (una vez, permanente en PowerShell):

```
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\ruta\a\tu\jdk", "User")
[Environment]::SetEnvironmentVariable(
  "Path",
  "$env:JAVA_HOME\bin;" + [Environment]::GetEnvironmentVariable("Path", "User"),
  "User"
)
```

Cerrar y abrir de nuevo la terminal, después:

```
java -version
```

Debe imprimir `openjdk version "21"` o superior.

### `java.io.IOException: Unable to establish loopback connection`

Aparece al correr Gradle desde ciertos entornos con restricciones de
red (sandbox de algunos IDEs, containers sin loopback habilitado).

**Solución**: correr Gradle desde una PowerShell / terminal
convencional del sistema, no desde el sandbox. Si el problema
persiste, agregar `--no-daemon` a la línea de Gradle:

```
.\gradlew run --no-daemon
```

### `failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine`

Aparece al correr `docker` en Windows cuando Docker Desktop no está
levantado.

**Solución**: abrir Docker Desktop desde el menú Inicio y esperar a
que arranque el motor (unos 15-30 segundos). Después:

```
docker ps
```

Debe listar los contenedores sin error.

## Al iniciar sesión

### "Correo o contraseña incorrectos, o la cuenta está inactiva."

Aparece al intentar iniciar sesión y una de tres cosas está pasando:

1. El correo no está registrado.
2. La contraseña no coincide.
3. La cuenta existe pero está desactivada.

**Solución**:

- Verificar que el correo sea correcto (case-insensitive no está
  garantizado en la comparación actual — se compara tal cual).
- Recuperar la contraseña no está implementado todavía (issue #56).
  Si el usuario olvidó su clave, un administrador puede consultar la
  base directamente:

  ```
  docker exec boletos-postgres psql -U boletos -d boletos \
    -c "SELECT correo, activo FROM usuarios WHERE correo = 'usuario@ejemplo.com';"
  ```

- Si `activo` es 0, un administrador puede reactivar la cuenta desde
  el Panel de Administración (pestaña Usuarios).

### "No se pudo conectar con la base de datos. Verifica que Docker esté en ejecución."

Aparece al iniciar sesión (o al abrir cualquier pantalla que hable
con la base) cuando el contenedor de Postgres no está corriendo.

**Solución**:

1. Verificar que Docker Desktop esté abierto.
2. `docker compose up -d` desde la raíz del repo.
3. Esperar 3-5 segundos y reintentar.

## En el Panel de Administración

### "No se pudieron cargar los datos. Verifica que Docker esté en ejecución."

Mismo caso que arriba. La ventana del panel se abre igual pero las
tablas quedan vacías. Cerrar la aplicación, levantar Docker y volver
a abrir.

### "Ya existe un evento con el nombre X."

Aparece al presionar **Agregar** con un nombre que ya está en la
base. El nombre es la llave primaria y no puede repetirse.

**Solución**: cambiar el nombre para que sea único.

### "El usuario ya está activo/inactivo."

Aparece al presionar **Activar** sobre un usuario ya activo, o
**Desactivar** sobre uno ya inactivo. No es un error, es una
validación defensiva.

### El reporte devuelve "No hay datos disponibles para la categoria 'X'"

La categoría es correcta pero no hay filas en `compras` con esa
categoría.

**Causas posibles**:

- Ningún cliente ha registrado una compra de esa categoría todavía.
- La conexión del flujo de compra (`CompraController.confirmarCompra`)
  todavía es un stub (issue #6, HU-05 pendiente en el sprint). Sin
  ese cableado, los clientes no pueden registrar compras reales.

**Solución para pruebas**: insertar compras manualmente:

```
docker exec boletos-postgres psql -U boletos -d boletos -c \
"INSERT INTO compras (correo_comprador, nombre_evento,
 categoria_evento, cantidad_boletos, total, fecha) VALUES
 ('carlos@boletos.com', 'Festival de Jazz', 'Musica', 2, 80.00,
  '2026-09-15');"
```

## En git y GitHub

### "Everything up-to-date" al hacer push pero el PR no muestra el commit

**Causa**: el push fue a otra rama por alguna redirección.

**Solución**:

```
git branch -vv
```

para ver qué rama remota está trackeando la local, y

```
git push origin nombre-local:nombre-remoto-correcto
```

si hace falta forzar el nombre.

### Pre-commit hook bloquea el commit

**Causa**: el hook local detectó una mención prohibida en el diff
o en el mensaje del commit. La lista de palabras vetadas la define
el equipo y se aplica sin excepciones.

**Solución**: quitar la mención antes de commitear. **No** usar
`--no-verify` — la regla existe por decisión del equipo. Ver el
script `.git/hooks/pre-commit` para saber exactamente qué patrones
se están buscando.

## En la base de datos

### La base está en un estado raro y hay que empezar de nuevo

**Solución nuclear** (borra todos los datos):

```
docker compose down -v
docker compose up -d
```

`down -v` elimina el volumen. La app volverá a crear las tablas y
los datos semilla en el próximo arranque.

### Necesito ver qué hay en una tabla sin abrir la app

```
docker exec boletos-postgres psql -U boletos -d boletos -c \
  "SELECT * FROM usuarios;"
```

Reemplazar `usuarios` por `eventos`, `compras` o `auditoria` según
la tabla que se quiera ver.

### El schema cambió y las migraciones no aplican

Depende de si Flyway (PR #62) está mergeado:

- **Sin Flyway** (estado actual de main): el schema se crea con
  `CREATE TABLE IF NOT EXISTS`. Cambios en el schema requieren
  borrar la tabla o el volumen (`docker compose down -v`).
- **Con Flyway**: agregar una nueva migración
  `V<n>__<descripcion>.sql` en `src/main/resources/db/migration/`
  y reiniciar la app. Flyway detecta y aplica las nuevas.

## Cuándo pedir ayuda al equipo

Si después de intentar la solución de arriba el error persiste,
abrir un issue con la plantilla de `.github/ISSUE_TEMPLATE/bug_report.md`
incluyendo:

- Sistema operativo y versión.
- Versión de Java (`java -version`).
- Rama y último commit (`git log -1 --oneline`).
- El error completo, tal cual salió.
- Los pasos exactos para reproducirlo.
