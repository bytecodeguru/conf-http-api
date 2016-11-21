package guru.bytecode.confhttpapi.rest;

import guru.bytecode.confhttpapi.model.EntityConflictException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityConflictExceptionMapper implements ExceptionMapper<EntityConflictException> {

    @Override
    public Response toResponse(EntityConflictException exception) {
        return Response.status(Response.Status.CONFLICT).build();
    }

}
