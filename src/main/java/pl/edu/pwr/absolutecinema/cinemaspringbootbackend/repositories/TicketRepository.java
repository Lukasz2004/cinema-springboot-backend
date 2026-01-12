package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
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
    public BigDecimal createTicket(BigDecimal seansId, BigDecimal zamowienieId, BigDecimal rodzajeZnizekId)
    {
        BigDecimal newTicketId = jdbc.execute(
                "{ call dodajBiletDoZamowienia(?, ?, ?, ?) }",
                (CallableStatement cs) -> {

                    cs.setBigDecimal(1, zamowienieId);
                    cs.setBigDecimal(2, seansId);
                    cs.setBigDecimal(3, rodzajeZnizekId);
                    cs.registerOutParameter(4, Types.NUMERIC);

                    cs.execute();
                    return cs.getBigDecimal(4);
                }
        );
        return newTicketId;
    }
}
