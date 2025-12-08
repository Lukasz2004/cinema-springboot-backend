package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private final String LOREM_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
            "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
            "nisi ut aliquip ex ea commodo consequat.";

    @GetMapping("/test/helloWorld")
    public String helloWorld() {
        return "Hello World!";
    }
    @GetMapping("/test/loremIpsum")
    public String lorem(@RequestParam int iloscZnakow) {
        if (iloscZnakow > LOREM_TEXT.length()) {
            return LOREM_TEXT;
        }

        if (iloscZnakow < 0) {
            return "Błąd: Ilość znaków nie może być ujemna!";
        }

        return LOREM_TEXT.substring(0, iloscZnakow);

    }
}
