package guru.bytecode.confhttpapi.rest;

import guru.bytecode.confhttpapi.model.Configuration;
import guru.bytecode.confhttpapi.model.CrudRepository;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationResource {
    
    private final CrudRepository<String, Configuration> repo;

    @Autowired
    ConfigurationResource(CrudRepository<String, Configuration> repo) {
        this.repo = repo;
    }
    
    @GET
    public @Valid List<Configuration> getAll() {
        return repo.getAll();
    }
    
    @GET
    @Path("{id}")
    public @Valid Optional<Configuration> get(String id) {
        return repo.read(id);
    }
}
