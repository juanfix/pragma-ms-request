package co.com.pragma.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userWebClient(
            WebClient.Builder builder,
            @Value("${ms-authentication.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
