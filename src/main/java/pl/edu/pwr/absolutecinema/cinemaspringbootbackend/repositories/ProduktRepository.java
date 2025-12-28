package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class ProduktRepository {
    private final JdbcTemplate jdbc;

    public ProduktRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    public Map<String, Object> findById(BigDecimal produktID) {
        return jdbc.queryForMap("SELECT * FROM produkt WHERE produktID = ?", produktID);
    }
    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM produkt ORDER BY PRODUKT.TYPPRODUKTU");
    }
    public Map<String, Object> filterId(Map<String, Object> toFiter)
    {
        toFiter.remove("produktID");
        return toFiter;
    }
    public List<Map<String, Object>> groupFilterId(List<Map<String, Object>> toFiter)
    {
        for (Map<String, Object> map : toFiter) {
            map.remove("produktID");
        }
        return toFiter;
    }
}
