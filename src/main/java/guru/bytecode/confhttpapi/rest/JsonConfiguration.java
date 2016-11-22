package guru.bytecode.confhttpapi.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.bytecode.confhttpapi.model.Configuration;
import javax.validation.constraints.NotNull;

public class JsonConfiguration implements Configuration {
    
    @NotNull
    private final String id;
    @NotNull
    private final String name;
    @NotNull
    private final String value;

    @JsonCreator
    JsonConfiguration(
            @JsonProperty("id") String id, 
            @JsonProperty("name") String name, 
            @JsonProperty("value") String value
    ) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
}
