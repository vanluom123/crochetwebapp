package org.crochet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Crochet",
                        email = "contact@crochet.com",
                        url = "littlecrochet.com"
                ),
                description = "OpenApi documentation for crochet application",
                title = "Crochet OpenApi specification",
                version = "1.0.0"
        )
)
@SecuritySchemes({
        @SecurityScheme(
                name = "BearerAuth",
                description = "JWT auth description",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT",
                in = SecuritySchemeIn.COOKIE
        )
})
public class OpenAPIConfig {
}
