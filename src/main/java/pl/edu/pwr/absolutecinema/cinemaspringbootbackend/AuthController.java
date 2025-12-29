package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth.CustomUserDetailsService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth.JwtService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth.Klient;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth.UserRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth.*;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.AuthService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AuthService authService;

    // Konstruktor
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetailsService customUserDetailsService, UserRepository userRepository, EmployeeRepository employeeRepository,  AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Loguje użytkownika na podstawie email i haslo", description = "Do logowania korzystamy z <b>tokenów JWT.</b> \n\n Aby zalogować, wysyłamy zapytanie na to API podając email i hasło w body. Jeśli dane są poprawne w odpowiedzi dostaniemy token JWT, który należy zapisać i dołączać do wszystkich zapytań wymagających logowania. \n\n Token taki ważny jest 10 godzin. \n\n Aby wysyłać zapytania autoryzowane w GUI Swaggera należy wstawić odpowiedź tego API do pola Authorize. ")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    @ApiResponse(responseCode = "200", description = "Zalogowano! Zwracam token JWT", content = @Content(examples = @ExampleObject(value = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW4ua293YWxza2lAZXhhbXBsZS5jb20iLCJpYXQiOjE3NjUzODIyMjYsImV4cCI6MTc2NTQxODIyNn0.U78Za49A6YAsuzZeY5bB8E2YTcNS07ERcsWdVxT7xgI")))
    @ApiResponse(responseCode = "401", description = "Nie zalogowano - Błędne dane", content = @Content(examples = @ExampleObject(value = "Błędne hasło lub email")))
    @ApiResponse(responseCode = "500", description = "Nie zalogowano - Inny błąd", content = @Content(schema = @Schema(hidden = true)))

    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        System.out.println("--- OTRZYMANO REQUEST LOGIN ---");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Hasło (jawne): " + request.getHaslo());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getHaslo())
            );
            System.out.println("--- UWIERZYTELNIENIE UDANE ---");

            String token = jwtService.generateToken(request.getEmail());
            return ResponseEntity.ok(token);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            System.out.println("--- BŁĄD: BadCredentialsException (Złe hasło/login) ---");
            return ResponseEntity.status(401).body("Błędne hasło lub email");
        } catch (Exception e) {
            System.out.println("--- BŁĄD INNY: " + e.getClass().getName() + " ---");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Wewnętrzny błąd: " + e.getMessage());
        }
    }

    @GetMapping("/loggedInProfile")
    @Operation(summary = "Zwraca szczegóły o zalogowanym użytkowniku na podstawie tokena JWT", description = "Do logowania korzystamy z <b>tokenów JWT.</b> \n\n Wymagane jest wcześniejsze zalogowanie. Token do zapytania dołączamy jako header \"Authorization\" w formacie \"Bearer TOKEN\" ")
    @ApiResponse(responseCode = "200", description = "Przesyłam szczegóły", content = @Content(examples = @ExampleObject(value = "{\"imie\":\"Jan\",\"nazwisko\":\"Kowalski\",\"poziomlojalnosciowy\":1}")))
    @ApiResponse(responseCode = "403", description = "Nie poprawnie zalogowano", content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }

        String email = userDetails.getUsername();

        boolean isPracownik = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Pracownik"));

        //PRACOWNIK
        if (isPracownik) {
            Pracownik pracownik = employeeRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Błąd danych pracownika"));

            EmployeeResponse response = new EmployeeResponse(
                    pracownik.getImie(),
                    pracownik.getNazwisko(),
                    pracownik.getRolapracownika(),
                    pracownik.isCzyaktywny()
            );
            return ResponseEntity.ok(response);

        //KLIENT
        } else {
            Klient klient = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Błąd danych klienta"));

            UserResponse response = new UserResponse(
                    klient.getImie(),
                    klient.getNazwisko(),
                    klient.getPoziomlojalnosciowy()
            );
            return ResponseEntity.ok(response);
        }
    }
    @PostMapping("/register")
    @Operation(summary = "Rejestruje nowego klienta", description = "Wymaga wszystkich podanych parametrów. Waliduje ich poprawność.")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public void registerUser(@Parameter(description = "Imię nowego klienta") @RequestParam String imie,
                                                  @Parameter(description = "Nazwisko nowego klienta.") @RequestParam String nazwisko,
                                                  @Parameter(description = "Adres email nowego klienta. Musi być unikalny.") @RequestParam String email,
                                                  @Parameter(description = "Hasło nowego klienta w formie czystej. Zostanie zaszyfrowane.") @RequestParam String haslo)
    {
        authService.registerClient(imie,nazwisko,email,haslo);
    }
}

class AuthRequest {
    @Getter
    @Setter
    @Schema(example = "jan.kowalski@example.com")
    private String email;
    @Getter
    @Setter
    @Schema(example = "haslo123")
    private String haslo;
}

@Data
@AllArgsConstructor
class UserResponse {
    @Getter
    @Setter
    @Schema(example = "Jan")
    private String imie;
    @Getter
    @Setter
    @Schema(example = "Kowalski")
    private String nazwisko;
    @Getter
    @Setter
    @Schema(example = "2")
    private int poziomlojalnosciowy;
}

@Data
@AllArgsConstructor
class EmployeeResponse {
    @Getter
    @Setter
    @Schema(example = "Piotr")
    private String imie;
    @Getter
    @Setter
    @Schema(example = "Kasprzak")
    private String nazwisko;
    @Getter
    @Setter
    @Schema(example = "Kasjer")
    private String rolapracownika;
    @Getter
    @Setter
    @Schema(example = "true")
    private boolean czyaktwny;
}
