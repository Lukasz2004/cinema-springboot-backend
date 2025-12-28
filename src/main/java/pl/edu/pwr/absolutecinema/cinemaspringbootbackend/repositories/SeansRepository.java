package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class SeansRepository {
    private final JdbcTemplate jdbc;

    public SeansRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    public Map<String, Object> findById(BigDecimal seansID) {
        return jdbc.queryForMap("SELECT * FROM seans WHERE seansID = ?", seansID);
    }
    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM seans");
    }
    public List<Map<String, Object>> findXForMovie(BigDecimal filmID, int amount) {
        if(amount == -1)
        {
            return findAllForMovie(filmID);
        }
        return jdbc.queryForList("SELECT * FROM V_AKTYWNESEANSE WHERE filmID = ? FETCH FIRST ? ROWS ONLY;", filmID, amount);
    }
    public List<Map<String, Object>> findAllForMovie(BigDecimal filmID) {
        return jdbc.queryForList("SELECT * FROM V_AKTYWNESEANSE WHERE filmID = ? ", filmID);
    }


}
