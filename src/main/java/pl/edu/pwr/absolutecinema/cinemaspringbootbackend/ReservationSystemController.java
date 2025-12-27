package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservation")
public class ReservationSystemController {

    private final JdbcTemplate jdbcTemplate;
    public ReservationSystemController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @GetMapping("/public/przekaski")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    @Operation(summary = "Zwraca wszystkie dostepne przekaski")
    public List<Map<String, Object>> listaPrzekasek() {
        String sql = "SELECT * FROM PRODUKT ORDER BY PRODUKT.TYPPRODUKTU";
        return jdbcTemplate.queryForList(sql);
    }


}
