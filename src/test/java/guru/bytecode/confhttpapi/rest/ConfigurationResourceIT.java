package guru.bytecode.confhttpapi.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.bytecode.confhttpapi.ConfHttpApi;
import guru.bytecode.confhttpapi.model.CrudRepository;
import guru.bytecode.confhttpapi.model.EntityConflictException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ConfHttpApi.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ConfigurationResourceIT {

    private static final TypeReference<List<JsonConfiguration>> LIST_CONFIGURATION_TYPE = new TypeReference<List<JsonConfiguration>>() {
    };

//    @LocalServerPort int localServerPort;
    int localServerPort = 8080;

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private ObjectMapper jackson;

//    @Autowired
//    private CrudRepository<String, JsonConfiguration> repo;
    @Before
    public void getAllIsEmpty() throws IOException {
        String json = client.getForObject(path("/"), String.class);
        List<JsonConfiguration> configurationList = jackson.readValue(json, LIST_CONFIGURATION_TYPE);
        assertThat(configurationList).isEmpty();
    }

    private URI path(String path) throws UriBuilderException, IllegalArgumentException {
        return getBaseUriBuilder().path(path).build();
    }

    @Test
    public void getAllIsNotEmpty() throws IOException, EntityConflictException {
        JsonConfiguration configuration = new JsonConfiguration("1", "name", "value");
        repoCreate(configuration);
        String json = client.getForObject(path("/"), String.class);
        List<JsonConfiguration> configurationList = jackson.readValue(json, LIST_CONFIGURATION_TYPE);
        assertThat(configurationList)
                .usingFieldByFieldElementComparator()
                .containsOnly(configuration);
        repoDelete("1");
    }

    private void repoDelete(String id) {
        RequestEntity<Void> request = RequestEntity
                .delete(path(id))
                .build();
        assertHttpStatus(
                client.exchange(request, Void.class),
                HttpStatus.NO_CONTENT
        );
    }

    private void repoCreate(JsonConfiguration configuration) throws EntityConflictException {
        ResponseEntity<Void> response = client.postForEntity(path("/"), configuration, Void.class);
        assertHttpStatus(response, HttpStatus.CREATED);
    }

    @Test
    public void getNonExistent() {
        assertHttpStatus(
                client.getForEntity(path("/1"), JsonConfiguration.class),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    public void getExistent() throws EntityConflictException {
        JsonConfiguration configuration = new JsonConfiguration("1", "name", "value");
        repoCreate(configuration);
        assertThat(client.getForObject(path("/1"), JsonConfiguration.class))
                .isEqualToComparingFieldByField(configuration);
        repoDelete("1");
    }

    @Test
    public void postNonExistent() {
        JsonConfiguration configuration = new JsonConfiguration("1", "name", "value");
        ResponseEntity<Void> response = client.postForEntity(path("/"), configuration, Void.class);
        assertHttpStatus(response, HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).hasPath("/1");
        repoDelete("1");
    }

    @Test
    public void postExistent() throws EntityConflictException {
        JsonConfiguration configuration = new JsonConfiguration("1", "name", "value");
        repoCreate(configuration);
        assertHttpStatus(
                client.postForEntity(path("/"), configuration, Void.class),
                HttpStatus.CONFLICT
        );
        repoDelete("1");
    }

    @Test
    public void postInvalid() throws EntityConflictException {
        JsonConfiguration configuration = new JsonConfiguration(null, "name", "value");
        assertHttpStatus(
                client.postForEntity(path("/"), configuration, Void.class),
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    public void putNonExistent() {
        JsonConfiguration configuration = new JsonConfiguration("1", "name", "value");
        RequestEntity<JsonConfiguration> request = RequestEntity
                .put(path("1"))
                .body(configuration);
        assertHttpStatus(
                client.exchange(request, Void.class),
                HttpStatus.NOT_FOUND
        );
        assertThat(repoRead("1")).isEmpty();
    }

    private Optional<JsonConfiguration> repoRead(String id) {
        ResponseEntity<JsonConfiguration> response = client.getForEntity(path("/" + id), JsonConfiguration.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return Optional.ofNullable(response.getBody());
        } else {
            return Optional.empty();
        }
    }

    @Test
    public void putExistent() throws EntityConflictException {
        JsonConfiguration configuration = new JsonConfiguration("1", "name", "value");
        repoCreate(configuration);
        JsonConfiguration updated = new JsonConfiguration("1", "name2", "value2");
        RequestEntity<JsonConfiguration> request = RequestEntity
                .put(path("1"))
                .body(updated);
        assertHttpStatus(
                client.exchange(request, Void.class),
                HttpStatus.NO_CONTENT
        );
        assertThat(repoRead("1")).hasValueSatisfying(c
                -> assertThat(c).isEqualToComparingFieldByField(updated)
        );
        repoDelete("1");
    }

    @Test
    public void putConflict() throws EntityConflictException {
        JsonConfiguration configuration = new JsonConfiguration("1", "name", "value");
        repoCreate(configuration);
        JsonConfiguration updated = new JsonConfiguration("2", "name", "value");
        RequestEntity<JsonConfiguration> request = RequestEntity
                .put(path("1"))
                .body(updated);
        assertHttpStatus(
                client.exchange(request, Void.class),
                HttpStatus.CONFLICT
        );
        assertThat(repoRead("1")).hasValueSatisfying(c
                -> assertThat(c).isEqualToComparingFieldByField(configuration)
        );
        assertThat(repoRead("2")).isEmpty();
        repoDelete("1");
    }

    @Test
    public void deleteExistent() throws EntityConflictException {
        JsonConfiguration configuration = new JsonConfiguration("1", "name", "value");
        repoCreate(configuration);
        RequestEntity<Void> request = RequestEntity
                .delete(path("1"))
                .build();
        assertHttpStatus(
                client.exchange(request, Void.class),
                HttpStatus.NO_CONTENT
        );
        assertThat(repoRead("1")).isEmpty();
    }

    @Test
    public void deleteNonExistent() throws EntityConflictException {
        RequestEntity<Void> request = RequestEntity
                .delete(path("1"))
                .build();
        assertHttpStatus(
                client.exchange(request, Void.class),
                HttpStatus.NOT_FOUND
        );
    }

    private UriBuilder getBaseUriBuilder() {
        return UriBuilder.fromUri(getBaseUri());
    }

    private URI getBaseUri() {
        return URI.create("http://localhost:" + localServerPort);
    }

    private static void assertHttpStatus(ResponseEntity<?> response, HttpStatus expected) {
        assertThat(response.getStatusCode()).isEqualTo(expected);
    }

}
