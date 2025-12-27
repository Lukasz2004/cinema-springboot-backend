package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    private final String LOREM_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
            "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
            "nisi ut aliquip ex ea commodo consequat.";

    @GetMapping("/helloWorld")
    @Operation(summary = "Zwraca \"Hello World!\"")
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("/loremIpsum")
    @Operation(summary = "Zwraca określoną parametrem iloscZnakow długość Lorem Ipsum")
    @ApiResponse(responseCode = "200", description = "Ok", content = @Content(examples = @ExampleObject(value = "Lorem ipsum dolor sit amet")))
    @ApiResponse(responseCode = "403", description = "Nie zalogowano", content = @Content(examples = @ExampleObject(value = "{\n\"status\": 403,\n\"error\": \"Forbidden\n\"}")))
    public String lorem(
            @Parameter(description = "Ilość znaków do zwrócenia")
            @RequestParam int iloscZnakow) {
        if (iloscZnakow > LOREM_TEXT.length()) {
            return LOREM_TEXT;
        }

        if (iloscZnakow < 0) {
            return "Błąd: Ilość znaków nie może być ujemna!";
        }

        return LOREM_TEXT.substring(0, iloscZnakow);

    }
}
