package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class FilmRepository {
    private final JdbcTemplate jdbc;

    public FilmRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> findById(BigDecimal filmId) {
        return jdbc.queryForMap("SELECT * FROM film WHERE filmID = ?", filmId);
    }
    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM film");
    }
    public List<Map<String, Object>> findAllActive() {
        return jdbc.queryForList("SELECT * FROM v_AktywneFilmy");
    }
}
