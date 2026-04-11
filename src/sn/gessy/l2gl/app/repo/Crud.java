package sn.gessy.l2gl.app.repo;

import java.util.List;
import java.util.Optional;
import sn.gessy.l2gl.app.model.Identifiable;

public interface Crud<T extends Identifiable> {

    void create(T entity);

    Optional<T> readOpt(Long id);

    void update(T entity);

    void delete(Long id);

    List<T> findAll();
}
