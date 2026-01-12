package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class ZamowienieRepository {
    private final JdbcTemplate jdbc;

    public ZamowienieRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    public BigDecimal createOrder(BigDecimal klientId, BigDecimal pracownikId,  String status, String metodaPlatnosci)
    {
        BigDecimal newOrderId = jdbc.execute(
                "{ call utworzZamowienie(?, ?, ?, ?, ?) }",
                (CallableStatement cs) -> {

                    cs.setBigDecimal(1, pracownikId);
                    cs.setBigDecimal(2, klientId);
                    cs.setString(3, metodaPlatnosci);
                    cs.setString(4, status);
                    cs.registerOutParameter(5, Types.NUMERIC);

                    cs.execute();
                    return cs.getBigDecimal(5);
                }
        );
        return newOrderId;
    }
    public void updateCena(BigDecimal zamowienieId, BigDecimal cenaKoncowa)
    {
        String sql = "UPDATE Zamowienie SET KWOTALACZNA = ? WHERE zamowienieID = ?";
        jdbc.update(sql, cenaKoncowa, zamowienieId);
    }

}
