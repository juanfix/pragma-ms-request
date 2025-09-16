package co.com.pragma.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LambdaWebClientConfig {

    @Bean
    public WebClient lambdaWebClient(
            WebClient.Builder builder,
            @Value("${entrypoint.lambda.base-url}") String lambdaBaseUrl) {
        return builder.baseUrl(lambdaBaseUrl).build();
    }
}
