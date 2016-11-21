package guru.bytecode.confhttpapi.rest;

import guru.bytecode.confhttpapi.model.CrudRepository;
import guru.bytecode.confhttpapi.model.EntityConflictException;
import guru.bytecode.confhttpapi.model.EntityNotFoundException;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigurationResource {

    private final CrudRepository<String, JsonConfiguration> repo;

    @Autowired
    ConfigurationResource(CrudRepository<String, JsonConfiguration> repo) {
        this.repo = repo;
    }

    @GET
    @Valid
    public List<JsonConfiguration> getAll() {
        return repo.getAll();
    }

    @POST
    public Response create(@Context UriInfo uriInfo, @Valid JsonConfiguration c)
            throws EntityConflictException {
        repo.create(c);
        return Response.created(URI.create(c.getId())).build();
    }

    @GET
    @Path("{id}")
    @Valid
    public JsonConfiguration get(@PathParam("id") String id) {
        return repo.read(id).orElseThrow(() -> new NotFoundException(id));
    }

    @PUT
    @Path("{id}")
    public void update(@PathParam("id") String id, @Valid JsonConfiguration c)
            throws EntityNotFoundException, EntityConflictException {
        if (!c.getId().equals(id)) {
            if (repo.read(id).isPresent()) {
                throw new EntityConflictException();
            } else {
                throw new EntityNotFoundException();
            }
        } else {
            repo.update(c);
        }
    }

    @DELETE
    @Path("{id}")
    public void update(@PathParam("id") String id) {
        repo.delete(id);
    }

}
