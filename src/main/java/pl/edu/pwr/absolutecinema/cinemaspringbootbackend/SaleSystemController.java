package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sale")
public class SaleSystemController {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    public SaleSystemController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }


    @GetMapping("/getBilet/{idBiletu}")
    @Operation(summary = "Zwraca dane o bilecie wskazanym po id parametrem idBiletu" , description = "Zwraca wszystkie dane widoku v_pokazBilet. \n\nDo logowania korzystamy z <b>tokenów JWT.</b> \n\n Wymagane jest wcześniejsze zalogowanie pracownika. Token do zapytania dołączamy jako header \"Authorization\" w formacie \"Bearer TOKEN\"")
    public List<Map<String, Object>> getBilet(@Parameter(description = "idBiletu do zwrócenia")@PathVariable int idBiletu) {
        return getBiletNoAuth(idBiletu);
    }
    @GetMapping("/legacy/getBilet/{idBiletu}")
    @Operation(summary = "Jak getBilet ale bez autoryzacji", description = "Dokładny opis w getBilet")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public List<Map<String, Object>> getBiletNoAuth(@PathVariable int idBiletu) {
        String sql = "SELECT * FROM v_PokazBIlet WHERE biletID = ?";
        return jdbcTemplate.queryForList(sql, idBiletu);
    }

}
