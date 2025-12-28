package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class AktorRepository {
    private final JdbcTemplate jdbc;

    public AktorRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    public Map<String, Object> findById(BigDecimal aktorID) {
        return jdbc.queryForMap("SELECT * FROM aktor WHERE aktorID = ?", aktorID);
    }
    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM aktor");
    }
    public Map<String, Object> filterId(Map<String, Object> toFiter)
    {
        toFiter.remove("AKTORID");
        return toFiter;
    }

    public List<Map<String, Object>> findAllForMovie(BigDecimal filmID) {
        return jdbc.queryForList("SELECT a.imieLubPseudonim, a.nazwisko, a.krajPochodzenia, a.dataUrodzenia FROM FILMAKTOR JOIN Aktor a ON FilmAktor.aktorID = a.aktorID WHERE filmID = ?", filmID);
    }
}
