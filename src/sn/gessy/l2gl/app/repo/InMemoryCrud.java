package sn.gessy.l2gl.app.repo;

import java.util.*;
import sn.gessy.l2gl.app.model.Identifiable;

public class InMemoryCrud<T extends Identifiable> implements Crud<T> {

    private final Map<Long, T> storage = new LinkedHashMap<>();

    @Override
    public void create(T entity) {
        if (entity == null)
            throw new IllegalArgumentException("Impossible de créer une entité null");
        if (entity.getId() == null)
            throw new IllegalArgumentException("L'id de l'entité ne peut pas être null");
        if (storage.containsKey(entity.getId()))
            throw new IllegalStateException(
                "Un enregistrement avec l'id " + entity.getId() + " existe déjà");
        storage.put(entity.getId(), entity);
    }

    @Override
    public Optional<T> readOpt(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void update(T entity) {
        if (entity == null)
            throw new IllegalArgumentException("Impossible de mettre à jour une entité null");
        if (!storage.containsKey(entity.getId()))
            throw new NoSuchElementException(
                "Aucun enregistrement avec l'id " + entity.getId() + " — update impossible");
        storage.put(entity.getId(), entity);
    }

    @Override
    public void delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("L'id ne peut pas être null pour supprimer");
        if (!storage.containsKey(id))
            throw new NoSuchElementException(
                "Aucun enregistrement avec l'id " + id + " — suppression impossible");
        storage.remove(id);
    }

    @Override
    public List<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    public int size() {
        return storage.size();
    }
}
