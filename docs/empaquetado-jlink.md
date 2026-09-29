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

    exports controladores to javafx.fxml;
    exports model;

    opens controladores to javafx.fxml;
    opens resources to javafx.fxml;
}
```

Los `opens` son necesarios para que JavaFX pueda inyectar los campos
`@FXML` mediante reflexión.

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
