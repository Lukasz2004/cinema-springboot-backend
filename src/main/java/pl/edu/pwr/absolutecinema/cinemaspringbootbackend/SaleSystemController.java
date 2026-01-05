package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.ProduktRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.SeansRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.ZnizkaRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.AuthService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.RepertuarService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.TicketService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sale")
public class SaleSystemController {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final RepertuarService repertuarService;
    private final ProduktRepository produktRepository;
    private final SeansRepository seansRepository;
    private static final String securityNoticeString = "Do logowania korzystamy z <b>tokenów JWT.</b> \n\n Wymagane jest wcześniejsze zalogowanie pracownika. Token do zapytania dołączamy jako header \"Authorization\" w formacie \"Bearer TOKEN\"";
    private final ZnizkaRepository znizkaRepository;
    private final TicketService ticketService;

    public SaleSystemController(JdbcTemplate jdbcTemplate, AuthService authService, RepertuarService repertuarService, ProduktRepository produktRepository, SeansRepository seansRepository, ZnizkaRepository znizkaRepository, TicketService ticketService) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
        this.authService = authService;
        this.repertuarService = repertuarService;
        this.produktRepository = produktRepository;
        this.seansRepository = seansRepository;
        this.znizkaRepository = znizkaRepository;
        this.ticketService = ticketService;
    }


    @GetMapping("/getBilet/{idBiletu}")
    @Operation(summary = "Zwraca dane o bilecie wskazanym po id parametrem idBiletu" , description = "Zwraca wszystkie dane widoku v_pokazBilet. \n\n" + securityNoticeString)
    public List<Map<String, Object>> getBilet(@Parameter(description = "idBiletu do zwrócenia")@PathVariable int idBiletu, @AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        String sql = "SELECT * FROM v_PokazBIlet WHERE biletID = ?";
        return jdbcTemplate.queryForList(sql, idBiletu);
    }

    @PatchMapping("/useBilet/{idBiletu}")
    @Operation(summary = "Ustawia status biletu o wskazanym po id parametrem idBiletu na \"Wykorzystany\"" , description = securityNoticeString)
    public void useBilet(@Parameter(description = "idBiletu do zużycia")@PathVariable int idBiletu, @AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        String sql = "UPDATE Bilet SET status = 'Wykorzystany' WHERE biletID = ?";
        jdbcTemplate.update(sql, idBiletu);
        return;
    }

    @GetMapping("/getMovies")
    @Operation(summary = "Zwraca pełen repertuar kina" , description = "Zwraca wszystkie filmy w kinie posiadające aktualne seanse.\n\n Dodatkowo dołącza: wszystkie możliwe języki seansów które się odbywają\n\n" + securityNoticeString)
    public List<Map<String, Object>> getMovies(@AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        return repertuarService.getRepertuarShort();
    }

    @GetMapping("/przekaski")
    @Operation(summary = "Zwraca wszystkie dostępne przekąski", description = securityNoticeString)
    public List<Map<String, Object>> listaPrzekasek(@AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        return produktRepository.groupFilterId(produktRepository.findAll());
    }
    @GetMapping("/getZnizki")
    @Operation(summary = "Zwraca wszystkie dostępne zniżki", description = "Zwraca całą zawartość tabeli RodzajeZniżek.\n\n" + securityNoticeString)
    public List<Map<String, Object>> getZnizki(@AuthenticationPrincipal UserDetails userDetails)
    {
        authService.verifyStaff(userDetails);
        return znizkaRepository.findAll();
    }
    @GetMapping("/getCennik/{idSeansu}")
    @Operation(summary = "Zwraca cene danego seansu rozpisując wszystkie dostępne zniżki", description = "Zwraca całą zawartość tabeli RodzajeZniżek dodając do każdej pole CENA określające dokładną cene tego seansu używając tej zniżki.\n\n" + securityNoticeString)
    public List<Map<String, Object>> getCennik(@Parameter(description = "idSeansu do sprawdzenia")@PathVariable int idSeansu,
                                                @AuthenticationPrincipal UserDetails userDetails)
    {
        authService.verifyStaff(userDetails);
        return ticketService.getCennik(idSeansu);
    }
    @GetMapping("/getSeanseForFilm/{id}")
    @Operation(summary = "Zwraca określoną parametrem iloscRekodow seansów dla filmu o podanym parametrem id", description = "Załącza dane z widoku v_AktywneSeanse z przedziału <zaczynajacOd, zaczynajacOd+iloscRekordow> \n\n" + securityNoticeString)
    public List<Map<String, Object>> getSeanseForFilm(@PathVariable int id,
                                                      @Parameter(description = "Od którego seansu zacząć.") @RequestParam(defaultValue = "0") int zaczynajacOd,
                                                      @Parameter(description = "Ilość seansów do zwrócenia. \n\n <b>-1 oznacza wszystkie możliwe</b>") @RequestParam(defaultValue = "-1") int iloscRekordow,
                                                      @AuthenticationPrincipal UserDetails userDetails)
    {
        authService.verifyStaff(userDetails);
        return seansRepository.findXForMovie(BigDecimal.valueOf(id),zaczynajacOd, iloscRekordow);
    }

}
