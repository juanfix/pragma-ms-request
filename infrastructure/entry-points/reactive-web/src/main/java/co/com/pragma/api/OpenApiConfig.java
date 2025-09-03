package co.com.pragma.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Pragma Template API",
        version = "1.0.0",
        description = "API for Pragma Template Application",
        contact = @Contact(
            name = "Pragma Team",
            email = "support@pragma.co",
            url = "https://pragma.co"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    )
)
public class OpenApiConfig {
    // La configuración se realiza mediante anotaciones
}
