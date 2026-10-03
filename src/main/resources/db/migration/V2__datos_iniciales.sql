-- Datos semilla: administrador por defecto y catalogo inicial de eventos.
-- Antes vivian en ConexionBD.sembrarDatosIniciales(). Se usan ON CONFLICT
-- para que la migracion sea idempotente en bases ya pobladas.

INSERT INTO usuarios (correo, nombre, clave, rol, activo) VALUES
    ('admin@boletos.com', 'Administrador General', 'admin123', 'ADMINISTRADOR', 1)
ON CONFLICT (correo) DO NOTHING;

INSERT INTO eventos (nombre_evento, categoria, fecha, lugar, inventario_disponible, precio_boleto) VALUES
    ('Concierto Rock Nacional', 'Musica', '2026-11-20', 'Estadio Cuscatlan', 45, 25.0),
    ('Festival de Jazz', 'Musica', '2026-10-15', 'Teatro Nacional', 26, 40.0),
    ('Obra de Teatro', 'Teatro', '2026-12-05', 'Teatro Presidente', 15, 15.5)
ON CONFLICT (nombre_evento) DO NOTHING;
