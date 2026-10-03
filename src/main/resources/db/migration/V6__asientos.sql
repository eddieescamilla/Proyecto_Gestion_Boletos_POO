-- Base de datos para la gestion de asientos numerados por evento (#60).
--
-- La UI todavia no ofrece seleccion de asientos; esta migracion deja la capa
-- de datos lista para que las proximas iteraciones agreguen el seat picker
-- en Compra.fxml y la pre-reserva al confirmar.
--
-- Composite PK (nombre_evento, numero_asiento). Al borrarse el evento,
-- cascada borra sus asientos. Un numero por asiento, 1..N dentro de cada
-- evento. Para los eventos ya existentes se siembran los N asientos a
-- partir del inventario_disponible actual del evento.

CREATE TABLE IF NOT EXISTS asientos (
    nombre_evento TEXT NOT NULL,
    numero_asiento INTEGER NOT NULL,
    vendido INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (nombre_evento, numero_asiento),
    CONSTRAINT fk_asiento_evento FOREIGN KEY (nombre_evento)
        REFERENCES eventos (nombre_evento)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_asientos_disponibles
    ON asientos (nombre_evento, vendido);

-- Siembra asientos para los eventos existentes sin tocar los que ya tengan
-- filas (idempotente ante re-ejecucion por errores previos).
INSERT INTO asientos (nombre_evento, numero_asiento, vendido)
SELECT e.nombre_evento, g.n, 0
  FROM eventos e
  CROSS JOIN LATERAL generate_series(1, GREATEST(e.inventario_disponible, 1)) AS g(n)
 WHERE NOT EXISTS (
   SELECT 1 FROM asientos a WHERE a.nombre_evento = e.nombre_evento
 );
