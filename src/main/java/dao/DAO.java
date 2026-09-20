package dao;

import java.util.List;

public interface DAO<T> {

    boolean guardar(T entidad);

    T buscarPorId(String id);

    List<T> listarTodos();

    boolean eliminar(String id);
}