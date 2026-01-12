package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
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
    public BigDecimal getCena(BigDecimal produktID, BigDecimal amount) {
        return jdbc.queryForObject("SELECT cena FROM produkt WHERE produktID = ?", BigDecimal.class, produktID).multiply(amount);
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
    public void createPozycjaOrder(BigDecimal zamowienieId, BigDecimal produktId, BigDecimal ilosc)
    {
        jdbc.execute(
                "{ call dodajProduktDoSprzedazy(?, ?, ?) }",
                (CallableStatement cs) -> {

                    cs.setBigDecimal(1, zamowienieId);
                    cs.setBigDecimal(2, produktId);
                    cs.setBigDecimal(3, ilosc);

                    cs.execute();
                    return null;
                }
        );
    }
}
