package platform.configurations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Bean
    public HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
