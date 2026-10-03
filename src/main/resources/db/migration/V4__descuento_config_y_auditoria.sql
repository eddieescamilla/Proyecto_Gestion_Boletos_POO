-- Agrega descuento_config (introducida en #70) y auditoria (introducida en #64).
-- Incluye el seed inicial de descuentos. El flag activo se guarda como INTEGER
-- (0 o 1) para mantener consistencia con las otras columnas booleanas del
-- esquema.

CREATE TABLE IF NOT EXISTS descuento_config (
    codigo TEXT PRIMARY KEY,
    tipo TEXT NOT NULL,
    valor REAL NOT NULL,
    activo INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS auditoria (
    id SERIAL PRIMARY KEY,
    fecha_hora TEXT NOT NULL,
    actor TEXT NOT NULL,
    accion TEXT NOT NULL,
    entidad TEXT NOT NULL,
    referencia TEXT,
    detalle TEXT
);

INSERT INTO descuento_config (codigo, tipo, valor, activo) VALUES
    ('DESC10', 'PORCENTAJE', 10.0, 1),
    ('DESC5',  'PORCENTAJE',  5.0, 1)
ON CONFLICT (codigo) DO NOTHING;
