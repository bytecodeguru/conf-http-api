package guru.bytecode.confhttpapi.model;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<I, E extends Entity<I>> {

    void create(E entity) throws EntityConflictException;

    void update(E entity) throws EntityNotFoundException;

    void delete(I id);

    Optional<E> read(I id);

    List<E> getAll();

}
