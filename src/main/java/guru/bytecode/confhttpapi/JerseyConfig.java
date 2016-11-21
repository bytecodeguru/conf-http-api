package guru.bytecode.confhttpapi;

import guru.bytecode.confhttpapi.rest.ConfigurationResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(ConfigurationResource.class);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    }

}
