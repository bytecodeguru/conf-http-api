package guru.bytecode.confhttpapi.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

public class InMemoryCrudRepository<I, E extends Entity<I>> implements CrudRepository<I, E> {
    
    private final Map<I, E> entityMap;

    public InMemoryCrudRepository() {
        this.entityMap = new ConcurrentSkipListMap<>();
    }

    @Override
    public void create(E entity) throws EntityConflictException {
        if(entityMap.putIfAbsent(entity.getId(), entity) != null) {
            throw new EntityConflictException();
        }
    }

    @Override
    public void update(E entity) throws EntityNotFoundException {
        if (entityMap.replace(entity.getId(), entity) == null) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void delete(I id) {
        entityMap.remove(id);
    }

    @Override
    public Optional<E> read(I id) {
        return Optional.ofNullable(entityMap.get(id));
    }

    @Override
    public List<E> getAll() {
        return new ArrayList<>(entityMap.values());
    }
    
}
