package co.com.pragma.webclient.helpers;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "ms-authentication")
public class MicroserviceProps {
    private String baseUrl;
    private String jwtSecretKey;
    private Long jwtExpiration;
}
