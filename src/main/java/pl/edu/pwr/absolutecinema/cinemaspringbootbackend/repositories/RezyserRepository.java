package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class RezyserRepository {
    private final JdbcTemplate jdbc;

    public RezyserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    public Map<String, Object> findById(BigDecimal rezyserId) {
        return jdbc.queryForMap("SELECT * FROM rezyser WHERE rezyserID = ?", rezyserId);
    }
    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM rezyser");
    }
    public Map<String, Object> filterId(Map<String, Object> toFiter)
    {
        toFiter.remove("REZYSERID");
        return toFiter;
    }
}
