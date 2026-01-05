package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class TicketRepository {
    private final JdbcTemplate jdbc;

    public TicketRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    public List<Map<String, Object>> findAllByKlientId(BigDecimal klientID) {
        return jdbc.queryForList("SELECT * FROM v_RezerwacjeKlienta WHERE klientId = ? ORDER BY DATACZAS", klientID);
    }
}
