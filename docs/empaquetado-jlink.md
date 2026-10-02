# Empaquetado con jlink

Guía para generar un ejecutable independiente del proyecto usando
`jlink`. El objetivo es producir una imagen de runtime que se pueda
distribuir sin requerir que el usuario final tenga Java instalado.

Este flujo aún no está integrado en `build.gradle` — cuando se agregue
esta guía se convierte en la referencia oficial.

## Requisitos previos

- JDK 21 o superior.
- El proyecto debe estar modularizado (archivo `module-info.java`).
- Gradle 9 o superior.

## Consideración importante: dependencias no modulares

`jlink` **rechaza automatic modules**: cualquier dependencia que no
declare su propio `module-info.class` no se puede incluir directamente.
El proyecto usa varias dependencias en esta situación, en particular:

- `org.postgresql:postgresql` (driver JDBC).
- `ch.qos.logback:logback-classic` (backend de logging).
- Algunas subdependencias transitivas.

Antes de correr `jlink` hay que resolver esto por alguna de las
siguientes vías:

1. **Envolver el driver con `moditect`**: el plugin
   [`org.moditect.gradleplugin`](https://github.com/moditect/moditect-gradle-plugin)
   permite generar un `module-info.java` sintético para cada
   dependencia no modular y publicarla como jar modular al build.
2. **Usar `jpackage` en lugar de `jlink`**: `jpackage` acepta
   automatic modules y produce un instalador nativo por sistema
   operativo. Está incluido en el JDK.
3. **Sacar el driver del module path**: dejarlo en el classpath del
   launcher (`--class-path`) y solo poner en el module path las
   dependencias modulares. Requiere ajustar la configuración de
   `org.beryx.jlink`.

Este documento describe la ruta con `jlink` puro. Si esa ruta se
descarta por complejidad, la alternativa recomendada es `jpackage`.

## 1. Agregar el plugin `org.beryx.jlink`

En `build.gradle`, dentro del bloque `plugins`:

```
plugins {
    id 'java'
    id 'application'
    id 'org.beryx.jlink' version '3.0.1'
}
```

## 2. Crear el `module-info.java`

En `src/main/java/module-info.java`:

```
module GestionBoletos {
    requires java.base;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    exports model;

    opens controladores to javafx.fxml;
}
```

El `opens controladores to javafx.fxml` es necesario para que JavaFX
pueda inyectar los campos `@FXML` en los controladores mediante
reflexión. Los archivos FXML viven en `src/main/resources/` (al nivel
raíz, sin subdirectorio) y no necesitan declaración `opens` en el
`module-info.java` porque se cargan como recursos del classpath, no
como clases Java.

## 3. Configurar `application` y `jlink`

```
application {
    mainClass = 'MainFX'
    mainModule = 'GestionBoletos'
}

jlink {
    imageName = 'GestionBoletos'
    launcher {
        name = 'GestionBoletos'
    }
}
```

## 4. Generar la imagen

```
./gradlew jlink
```

La imagen queda en `build/GestionBoletos/`. Contiene el runtime de
Java recortado y un lanzador ejecutable en `bin/GestionBoletos`
(`.bat` en Windows).

## 5. Ejecutar la app empaquetada

Desde la carpeta generada:

```
cd build/GestionBoletos/bin
./GestionBoletos       # Linux/macOS
GestionBoletos.bat     # Windows
```

## Notas

- La app requiere que PostgreSQL esté corriendo con las credenciales
  configuradas en `.env` o en las variables de entorno.
- La imagen resultante depende del sistema operativo donde se generó.
  Para distribuir en otras plataformas hay que regenerar en cada una,
  o usar `jpackage` para producir instaladores nativos.
- `logs/` se crea junto al ejecutable en la primera ejecución.
