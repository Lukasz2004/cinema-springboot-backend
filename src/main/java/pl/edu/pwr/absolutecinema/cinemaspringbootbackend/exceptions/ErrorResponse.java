package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reprezentacja błędu API")
public record ErrorResponse(
        @Schema(description = "Kod błędu") String code,
        @Schema(description = "Opis błędu") String message
) {}
