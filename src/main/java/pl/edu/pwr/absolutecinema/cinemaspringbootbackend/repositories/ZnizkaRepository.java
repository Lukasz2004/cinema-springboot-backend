package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class ZnizkaRepository {
    private final JdbcTemplate jdbc;

    public ZnizkaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    public Map<String, Object> findById(BigDecimal znizkaID) {
        return jdbc.queryForMap("SELECT * FROM rodzajeZnizek WHERE znizkaId = ?", znizkaID);
    }
    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM rodzajeZnizek ORDER BY procentZnizki ASC");
    }
}
