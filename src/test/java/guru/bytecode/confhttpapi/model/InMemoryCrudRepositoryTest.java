package guru.bytecode.confhttpapi.model;

import java.util.Objects;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InMemoryCrudRepositoryTest {
    
    private final InMemoryCrudRepository<String, Configuration> repo = new InMemoryCrudRepository<>();
    
    @Test
    public void repoIsEmpty() {
        assertThat(repo.getAll()).isEmpty();
    }
    
    @Test
    public void createNew() throws EntityConflictException {
        Configuration c = mockConfiguration("1");
        repo.create(c);
        assertThat(repo.read(c.getId()))
                .isNotEmpty()
                .hasValueSatisfying(c1 -> 
                        Objects.equals(c.getId(), c1.getId())
                );
    }
    
    @Test
    public void repoIsNotEmpty() throws EntityConflictException {
        Configuration c = mockConfiguration("1");
        repo.create(c);
        assertThat(repo.getAll()).isNotEmpty();
    }
    
    @Test
    public void createExisting() throws EntityConflictException {
        Configuration c = mockConfiguration("1");
        repo.create(c);
        assertThatThrownBy(() -> repo.create(c))
                .isInstanceOf(EntityConflictException.class);
    }
    
    @Test
    public void createTwo() throws EntityConflictException {
        Configuration c1 = mockConfiguration("1");
        Configuration c2 = mockConfiguration("2");
        repo.create(c1);
        repo.create(c2);
        assertThat(repo.getAll()).containsExactlyInAnyOrder(c1, c2);
    }
    
    @Test
    public void updateExisting() throws EntityConflictException, EntityNotFoundException {
        Configuration c = mockConfiguration("1");
        repo.create(c);
        Configuration u = mockConfiguration("1");
        repo.update(u);
        assertThat(repo.read("1")).hasValue(u);
    }
    
    @Test
    public void updateNotExisting() throws EntityNotFoundException {
        Configuration u = mockConfiguration("1");
        assertThatThrownBy(() -> repo.update(u))
                .isInstanceOf(EntityNotFoundException.class);
    }
    
    @Test
    public void deleteNotExisting() {
        repo.delete("1");
        assertThat(repo.read("1")).isEmpty();
    }
    
    @Test
    public void deleteExisting() throws EntityConflictException {
        Configuration c = mockConfiguration("1");
        repo.create(c);
        repo.delete("1");
        assertThat(repo.read("1")).isEmpty();
    }

    private Configuration mockConfiguration(String id) {
        Configuration c = mock(Configuration.class, id);
        when(c.getId()).thenReturn(id);
        return c;
    }
    
}
