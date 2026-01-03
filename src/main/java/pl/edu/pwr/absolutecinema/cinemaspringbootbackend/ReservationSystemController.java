package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.ProduktRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.SeansRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.RepertuarService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservation")
public class ReservationSystemController {

    private final JdbcTemplate jdbcTemplate;
    private final RepertuarService repertuarService;
    private final ProduktRepository produktRepository;
    private final SeansRepository seansRepository;

    public ReservationSystemController(JdbcTemplate jdbcTemplate, RepertuarService repertuarService, ProduktRepository produktRepository, SeansRepository seansRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.repertuarService = repertuarService;
        this.produktRepository = produktRepository;
        this.seansRepository = seansRepository;
    }
    @GetMapping("/public/przekaski")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    @Operation(summary = "Zwraca wszystkie dostępne przekąski")
    public List<Map<String, Object>> listaPrzekasek() {
        return produktRepository.groupFilterId(produktRepository.findAll());
    }

    @GetMapping("/public/getRepertuar")
    @Operation(summary = "Zwraca pełen repertuar kina", description = "Zwraca wszystkie filmy w kinie posiadające aktualne seanse.\n\n Dodatkowo dołącza: dane reżysera, dane aktorów oraz dane z widoku v_AktywneSeanse dla maks 5 najświeższych seansów z każdego filmu.")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public List<Map<String, Object>> getRepertuar() {
        return repertuarService.getRepertuar();
    }

    @GetMapping("/public/getSeanseForFilm/{id}")
    @Operation(summary = "Zwraca określoną parametrem iloscRekodow seansów dla filmu o podanym parametrem id", description = "Załącza dane z widoku v_AktywneSeanse z przedziału <zaczynajacOd, zaczynajacOd+iloscRekordow>")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public List<Map<String, Object>> getSeanseForFilm(@PathVariable int id,
                                                      @Parameter(description = "Od którego seansu zacząć.") @RequestParam(defaultValue = "0") int zaczynajacOd,
                                                      @Parameter(description = "Ilość seansów do zwrócenia. \n\n <b>-1 oznacza wszystkie możliwe</b>") @RequestParam(defaultValue = "-1") int iloscRekordow)
    {
        return seansRepository.findXForMovie(BigDecimal.valueOf(id),zaczynajacOd, iloscRekordow);
    }

}
