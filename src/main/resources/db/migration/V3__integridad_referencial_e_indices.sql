-- Refuerza la integridad referencial y agrega indices para las consultas
-- mas frecuentes (login, listado por correo, reporte por categoria y fecha).
--
-- Antes de agregar las FK se limpian huerfanos que pudieran haber quedado
-- de la etapa en la que compras.correo_comprador y compras.nombre_evento
-- eran texto libre.

DELETE FROM compras
 WHERE correo_comprador NOT IN (SELECT correo FROM usuarios)
    OR nombre_evento    NOT IN (SELECT nombre_evento FROM eventos);

ALTER TABLE compras
    ADD CONSTRAINT fk_compras_usuario
    FOREIGN KEY (correo_comprador) REFERENCES usuarios (correo)
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE compras
    ADD CONSTRAINT fk_compras_evento
    FOREIGN KEY (nombre_evento) REFERENCES eventos (nombre_evento)
    ON UPDATE CASCADE ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS idx_compras_categoria_fecha
    ON compras (categoria_evento, fecha);

CREATE INDEX IF NOT EXISTS idx_compras_correo
    ON compras (correo_comprador);

CREATE INDEX IF NOT EXISTS idx_eventos_categoria_fecha
    ON eventos (categoria, fecha);
