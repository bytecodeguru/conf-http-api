package guru.bytecode.confhttpapi;

import guru.bytecode.confhttpapi.model.CrudRepository;
import guru.bytecode.confhttpapi.model.InMemoryCrudRepository;
import guru.bytecode.confhttpapi.rest.JsonConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConfHttpApi {
    
    public static void main(String[] args) {
        SpringApplication.run(ConfHttpApi.class, args);
    }
    
    @Bean
    CrudRepository<String, JsonConfiguration> configurationRepository() {
        return new InMemoryCrudRepository<>();
    }
    
}
