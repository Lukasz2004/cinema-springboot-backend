package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.exceptions;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.info.BuildProperties;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI(BuildProperties buildProperties) {
        var schemaMap = ModelConverters.getInstance()
                .readAllAsResolvedSchema(ErrorResponse.class);
        Schema errorResponseSchema = schemaMap.schema;

        return new OpenAPI()
                .info(new Info()
                        .title("Absolute Cinema API")
                        .version(buildProperties.getVersion())
                        .description("System rezerwacji kinowej dla kina Absolute Cinema. "))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                        .addSchemas("ErrorResponse", errorResponseSchema)
                        .addResponses("Forbidden", new ApiResponse()
                                .description("Brak dostępu. Nie poprawny, bądź brak załączonego tokena JWT.")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType()
                                                        .schema(new Schema<ErrorResponse>()
                                                                .$ref("#/components/schemas/ErrorResponse")
                                                        )
                                        )
                                )
                        )
                        .addResponses("NotFound", new ApiResponse()
                                .description("Nie znaleziono zasobu")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType()
                                                        .schema(new Schema<ErrorResponse>()
                                                                .$ref("#/components/schemas/ErrorResponse")
                                                        )
                                        )
                                )
                        )
                        .addResponses("InternalError", new ApiResponse()
                                .description("Wewnętrzny błąd serwera")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType()
                                                        .schema(new Schema<ErrorResponse>()
                                                                .$ref("#/components/schemas/ErrorResponse")
                                                        )
                                        )
                                )
                        )
                );
    }

    @Bean
    public OpenApiCustomizer globalResponsesCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    operation.getResponses().addApiResponse("403", new ApiResponse().$ref("#/components/responses/Forbidden"));
                    operation.getResponses().addApiResponse("404", new ApiResponse().$ref("#/components/responses/NotFound"));
                    operation.getResponses().addApiResponse("500", new ApiResponse().$ref("#/components/responses/InternalError"));
                })
        );
    }
}