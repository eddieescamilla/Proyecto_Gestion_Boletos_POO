-- Cierra el hallazgo CWE-798 (hardcoded credentials) del scan externo: elimina
-- el admin sembrado por V2 cuando la clave sigue siendo la plana original
-- ("admin123"). Si la cuenta ya se migro a BCrypt en algun login previo, la
-- clave tiene mas de 50 caracteres y este DELETE no la toca.
--
-- A partir de ahora el administrador por defecto se siembra desde Java
-- (ConexionBD.sembrarAdminSiCorresponde) solo si la variable de entorno
-- BOLETOS_ADMIN_PASSWORD esta definida; para desarrollo local su valor va en
-- el .env (ver .env.example).

DELETE FROM usuarios
 WHERE correo = 'admin@boletos.com'
   AND LENGTH(clave) < 50;
