# Sistema de Gestión de Venta de Boletos para Eventos

## Descripción

Desarrollo de clases e implementación de patrones de diseño para estructurar y
gestionar el sistema de compra de boletos para eventos, promoviendo una
arquitectura modular y robusta que mejora la organización del sistema,
facilita su mantenimiento y optimiza la integración entre la lógica de
negocio, la interfaz gráfica y la persistencia de datos.

## Primer entregable

Diagramas de clases e implementación en Java.

## Estructura del proyecto
```
Proyecto_Gestion_Boletos_POO/
├── build.gradle
├── settings.gradle
├── eventos.txt              (no incluido en el repo, ver abajo)
└── src/
    └── main/
        └── java/
            ├── Main.java
            └── model/
                ├── Evento.java
                ├── Persona.java
                ├── Comprador.java
                ├── CompradorVIP.java
                ├── Compra.java
                ├── Descuento.java
                ├── DescuentoPorcentaje.java
                ├── DescuentoFijo.java
                └── SistemaGestionBoletos.java
```

## Funcionalidad principal

- Carga y guarda el listado de eventos en un archivo de texto.
- Permite seleccionar un evento y comprar boletos, validando stock disponible.
- Soporta comprador **VIP** (`CompradorVIP`), que recibe automáticamente un
  10% de descuento adicional sobre el total.
- Soporta códigos de descuento manuales: `DESC10` (10%) y `DESC5` (5%).

## Archivo `eventos.txt`

El sistema busca un archivo `eventos.txt` en la raíz del proyecto (mismo nivel
donde se ejecuta el programa). Cada línea representa un evento con el formato:

```
nombre|inventario|precio
```

Ejemplo:
```
Concierto Rock|100|25.5
Obra de Teatro|5|10.0
```

## Metodología de trabajo

Este proyecto se desarrolla siguiendo **Gitflow** como metodología de trabajo
colaborativo (ramas `main`, `develop`, `feature/*`).

## Cómo ejecutar

**Desde IntelliJ IDEA:** abrir el proyecto y ejecutar la clase `Main.java`.

**Desde la terminal:**
```bash
javac -d out src/main/java/Main.java src/main/java/model/*.java
cp eventos.txt out/
cd out
java Main
```