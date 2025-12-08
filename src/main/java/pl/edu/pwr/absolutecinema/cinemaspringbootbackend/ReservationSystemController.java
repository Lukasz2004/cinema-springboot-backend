package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ReservationSystemController {

    private final JdbcTemplate jdbcTemplate;
    public ReservationSystemController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @GetMapping("/oracle-test")
    public List<Map<String, Object>> getOracleData() {
        String sql = "SELECT * FROM FILM FETCH FIRST 10 ROWS ONLY";
        return jdbcTemplate.queryForList(sql);
    }
    @GetMapping("/przekaski")
    public List<Map<String, Object>> listaPrzekasek() {
        String sql = "SELECT * FROM PRODUKT ORDER BY PRODUKT.TYPPRODUKTU";
        return jdbcTemplate.queryForList(sql);
    }
}
