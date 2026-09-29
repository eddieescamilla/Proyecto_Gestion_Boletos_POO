-- Estructura inicial de las tablas principales del sistema.
-- Refleja lo que antes creaba ConexionBD.crearTablas() de forma programatica.

CREATE TABLE IF NOT EXISTS usuarios (
    correo TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    clave TEXT NOT NULL,
    rol TEXT NOT NULL,
    activo INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS eventos (
    nombre_evento TEXT PRIMARY KEY,
    categoria TEXT NOT NULL,
    fecha TEXT NOT NULL,
    lugar TEXT NOT NULL,
    inventario_disponible INTEGER NOT NULL,
    precio_boleto REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS compras (
    id SERIAL PRIMARY KEY,
    correo_comprador TEXT NOT NULL,
    nombre_evento TEXT NOT NULL,
    categoria_evento TEXT NOT NULL,
    cantidad_boletos INTEGER NOT NULL,
    total REAL NOT NULL,
    fecha TEXT NOT NULL
);
