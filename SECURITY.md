# Política de seguridad

## Versiones con soporte

Este es un proyecto académico del curso POO (TDS, UCA El Salvador). Solo la
versión en `main` recibe correcciones de seguridad. Las versiones anteriores
quedan congeladas para fines de evaluación.

| Versión | Soporte |
|---------|---------|
| 1.6.x   | ✅      |
| < 1.6   | ❌      |

## Reportar una vulnerabilidad

Si encontraste una vulnerabilidad, por favor **no** abras un issue público.
Reporta de alguna de estas formas:

1. Usa el flujo de reporte privado de GitHub en la pestaña **Security** del
   repositorio (opción *Report a vulnerability*).
2. Si no tenés acceso al repositorio, escribe a cualquiera de los
   mantenedores listados en el README (ver sección *Integrantes*).

En el reporte incluí:

- Versión afectada (ver el `CHANGELOG.md` o el `tag` del commit).
- Pasos para reproducir, con lo mínimo necesario.
- Impacto esperado si el reporte se confirma.
- Si tenés un parche propuesto, mejor; si no, no te preocupes.

## Qué esperar

- Respuesta inicial en un plazo razonable (es un proyecto académico, no un
  servicio con SLA).
- Si el reporte se confirma, el fix se publicará en un release nuevo y se
  documentará en el `CHANGELOG.md` sin exponer detalles explotables hasta
  que haya rotación de credenciales en las instalaciones conocidas.
- Si el reporte no se puede reproducir o cae fuera del scope del proyecto,
  se cierra con una explicación corta.

## Scope

Este proyecto es una aplicación de escritorio local; no hay servicio
publicado y no se almacenan datos de usuarios reales. Lo que sí nos importa:

- Credenciales por defecto o hardcodeadas en el código o la configuración.
- Fallas en la autenticación o la autorización del Panel de Administración.
- Inyección SQL, XSS en logs o controles de la GUI que acepten entrada
  arbitraria.
- Fugas de datos entre cuentas (p. ej. compras de otro usuario, roles
  elevados sin autorización).
- Lectura o manipulación remota del PostgreSQL cuando corre en localhost.

Fuera de scope (al menos para esta versión):

- Falta de TLS cuando se corre contra `localhost`.
- Reporting de dependencias con CVEs ya conocidos y abiertos (Dependabot ya
  los sigue).
- Fuzzing automático de la consola o de los Textfields de la GUI que no
  resulten en bypass de seguridad.
