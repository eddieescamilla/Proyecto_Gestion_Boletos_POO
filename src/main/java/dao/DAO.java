package dao;

import java.util.List;

/**
 * Contrato genérico del patrón DAO para las operaciones básicas de persistencia.
 *
 * @param <T> tipo de entidad que se persiste
 */
public interface DAO<T> {

  /**
   * Guarda una entidad nueva.
   *
   * @param entidad entidad a guardar
   * @return {@code true} si se guardó correctamente
   */
  boolean guardar(T entidad);

  /**
   * Busca una entidad por su identificador.
   *
   * @param id identificador de la entidad
   * @return la entidad encontrada, o {@code null} si no existe
   */
  T buscarPorId(String id);

  /**
   * Devuelve todas las entidades guardadas.
   *
   * @return lista con todas las entidades
   */
  List<T> listarTodos();

  /**
   * Elimina una entidad por su identificador.
   *
   * @param id identificador de la entidad
   * @return {@code true} si se eliminó alguna entidad
   */
  boolean eliminar(String id);
}