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
    public Map<String, Object> findByIdExtended(BigDecimal seansID) {
        return jdbc.queryForMap("SELECT s.dataCzas, f.tytul, f.czasTrwania, sa.nazwa AS Sala,t.nazwa AS Typ,s.jezykSeansu, s.DUBBINGCZYNAPISY, s.cenaSpecjalna FROM seans s JOIN Film f ON s.filmID=f.filmID JOIN Sala sa ON s.salaID=sa.salaID JOIN TypSeansu t ON s.typSeansuID=t.typSeansuID WHERE seansID = ?", seansID);
    }
    public Map<String, Object> findTypeById(BigDecimal typSeansuID) {
        return jdbc.queryForMap("SELECT * FROM TYPSEANSU WHERE TYPSEANSUID = ?", typSeansuID);
    }
    public List<Map<String, Object>> findAll() {
        return jdbc.queryForList("SELECT * FROM seans");
    }
    public List<Map<String, Object>> findXForMovie(BigDecimal filmID, int start, int amount) {
        if(amount == -1)
        {
            return findAllForMovie(filmID);
        }
        return jdbc.queryForList("SELECT * FROM V_AKTYWNESEANSE WHERE filmID = ? ORDER BY dataCzas ASC OFFSET ? ROWS FETCH FIRST ? ROWS ONLY", filmID, start, amount);
    }
    public List<Map<String, Object>> findAllForMovie(BigDecimal filmID) {
        return jdbc.queryForList("SELECT * FROM V_AKTYWNESEANSE WHERE filmID = ? ORDER BY dataCzas ASC", filmID);
    }
    public List<Map<String, Object>> findAllLanguagesForMovie(BigDecimal filmID) {
        return jdbc.queryForList("SELECT DISTINCT jezykSeansu, dubbingCzyNapisy FROM V_AKTYWNESEANSE WHERE filmID = ? ", filmID);
    }
    public BigDecimal findAvailabilityById(BigDecimal seansID) {
        return jdbc.queryForObject("SELECT liczbaWolnychMiejsc(?) AS wolne_miejsca FROM dual", BigDecimal.class, seansID);
    }


}
