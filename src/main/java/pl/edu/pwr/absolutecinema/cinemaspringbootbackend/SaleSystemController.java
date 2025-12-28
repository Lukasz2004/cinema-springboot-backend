package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.AuthService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.RepertuarService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sale")
public class SaleSystemController {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final RepertuarService repertuarService;
    public SaleSystemController(JdbcTemplate jdbcTemplate, AuthService authService, RepertuarService repertuarService) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
        this.authService = authService;
        this.repertuarService = repertuarService;
    }


    @GetMapping("/getBilet/{idBiletu}")
    @Operation(summary = "Zwraca dane o bilecie wskazanym po id parametrem idBiletu" , description = "Zwraca wszystkie dane widoku v_pokazBilet. \n\nDo logowania korzystamy z <b>tokenów JWT.</b> \n\n Wymagane jest wcześniejsze zalogowanie pracownika. Token do zapytania dołączamy jako header \"Authorization\" w formacie \"Bearer TOKEN\"")
    public List<Map<String, Object>> getBilet(@Parameter(description = "idBiletu do zwrócenia")@PathVariable int idBiletu, @AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        return getBiletNoAuth(idBiletu);
    }
    @GetMapping("/legacy/getBilet/{idBiletu}")
    @Operation(summary = "Jak getBilet ale bez autoryzacji", description = "Dokładny opis w getBilet")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public List<Map<String, Object>> getBiletNoAuth(@PathVariable int idBiletu) {
        String sql = "SELECT * FROM v_PokazBIlet WHERE biletID = ?";
        return jdbcTemplate.queryForList(sql, idBiletu);
    }

    @PatchMapping("/useBilet/{idBiletu}")
    @Operation(summary = "Ustawia status biletu o wskazanym po id parametrem idBiletu na \"Wykorzystany\"" , description = "Do logowania korzystamy z <b>tokenów JWT.</b> \n\n Wymagane jest wcześniejsze zalogowanie pracownika. Token do zapytania dołączamy jako header \"Authorization\" w formacie \"Bearer TOKEN\"")
    public void useBilet(@Parameter(description = "idBiletu do zużycia")@PathVariable int idBiletu, @AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        useBiletNoAuth(idBiletu);
        return;
    }
    @PatchMapping("/legacy/useBilet/{idBiletu}")
    @Operation(summary = "Jak checkBilet ale bez autoryzacji", description = "Dokładny opis w useBilet")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public void useBiletNoAuth(@PathVariable int idBiletu) {
        String sql = "UPDATE Bilet SET status = 'Wykorzystany' WHERE biletID = ?";
        jdbcTemplate.update(sql, idBiletu);
        return;
    }

    @GetMapping("/getMovies")
    @Operation(summary = "Zwraca pełen repertuar kina" , description = "Zwraca wszystkie filmy w kinie posiadające aktualne seanse.\n\n Dodatkowo dołącza: wszystkie możliwe języki seansów które się odbywają\n\nDo logowania korzystamy z <b>tokenów JWT.</b> \n\n Wymagane jest wcześniejsze zalogowanie pracownika. Token do zapytania dołączamy jako header \"Authorization\" w formacie \"Bearer TOKEN\"")
    public List<Map<String, Object>> getMovies(@AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        return repertuarService.getRepertuarShort();
    }
    @GetMapping("/legacy/getMovies")
    @Operation(summary = "Jak getMovies ale bez autoryzacji", description = "Dokładny opis w getMovies")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public List<Map<String, Object>> getMoviesNoAuth(@AuthenticationPrincipal UserDetails userDetails) {
        return repertuarService.getRepertuarShort();
    }
}
