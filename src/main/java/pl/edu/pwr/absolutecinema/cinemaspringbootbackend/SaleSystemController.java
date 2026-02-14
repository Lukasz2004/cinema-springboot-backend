package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.ProduktRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.SeansRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.ZnizkaRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.AuthService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.RepertuarService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.TicketService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sale")
public class SaleSystemController {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final RepertuarService repertuarService;
    private final ProduktRepository produktRepository;
    private final SeansRepository seansRepository;
    private static final String securityNoticeString = "Do logowania korzystamy z <b>tokenów JWT.</b> \n\n Wymagane jest wcześniejsze zalogowanie pracownika. Token do zapytania dołączamy jako header \"Authorization\" w formacie \"Bearer TOKEN\"";
    private final ZnizkaRepository znizkaRepository;
    private final TicketService ticketService;

    public SaleSystemController(JdbcTemplate jdbcTemplate, AuthService authService, RepertuarService repertuarService, ProduktRepository produktRepository, SeansRepository seansRepository, ZnizkaRepository znizkaRepository, TicketService ticketService) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
        this.authService = authService;
        this.repertuarService = repertuarService;
        this.produktRepository = produktRepository;
        this.seansRepository = seansRepository;
        this.znizkaRepository = znizkaRepository;
        this.ticketService = ticketService;
    }


    @GetMapping("/getBilet/{idBiletu}")
    @Operation(summary = "Zwraca dane o bilecie wskazanym po id parametrem idBiletu" , description = "Zwraca wszystkie dane widoku v_pokazBilet. \n\n" + securityNoticeString)
    public List<Map<String, Object>> getBilet(@Parameter(description = "idBiletu do zwrócenia")@PathVariable int idBiletu, @AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        String sql = "SELECT * FROM v_PokazBIlet WHERE biletID = ?";
        return jdbcTemplate.queryForList(sql, idBiletu);
    }

    @PatchMapping("/useBilet/{idBiletu}")
    @Operation(summary = "Ustawia status biletu o wskazanym po id parametrem idBiletu na \"Wykorzystany\"" , description = securityNoticeString)
    public void useBilet(@Parameter(description = "idBiletu do zużycia")@PathVariable int idBiletu, @AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        String sql = "UPDATE Bilet SET status = 'Wykorzystany' WHERE biletID = ?";
        jdbcTemplate.update(sql, idBiletu);
        return;
    }

    @GetMapping("/getMovies")
    @Operation(summary = "Zwraca pełen repertuar kina" , description = "Zwraca wszystkie filmy w kinie posiadające aktualne seanse.\n\n Dodatkowo dołącza: wszystkie możliwe języki seansów które się odbywają\n\n" + securityNoticeString)
    public List<Map<String, Object>> getMovies(@AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        return repertuarService.getRepertuarShort();
    }

    @GetMapping("/przekaski")
    @Operation(summary = "Zwraca wszystkie dostępne przekąski", description = securityNoticeString)
    public List<Map<String, Object>> listaPrzekasek(@AuthenticationPrincipal UserDetails userDetails) {
        authService.verifyStaff(userDetails);
        return produktRepository.findAll();
    }
    @GetMapping("/getZnizki")
    @Operation(summary = "Zwraca wszystkie dostępne zniżki", description = "Zwraca całą zawartość tabeli RodzajeZniżek.\n\n" + securityNoticeString)
    public List<Map<String, Object>> getZnizki(@AuthenticationPrincipal UserDetails userDetails)
    {
        authService.verifyStaff(userDetails);
        return znizkaRepository.findAll();
    }
    @GetMapping("/getCennik/{idSeansu}")
    @Operation(summary = "Zwraca cene danego seansu rozpisując wszystkie dostępne zniżki", description = "Zwraca całą zawartość tabeli RodzajeZniżek dodając do każdej pole CENA określające dokładną cene tego seansu używając tej zniżki. \n\nCena standardowa tego seansu (dla biletu normalnego) dodawana jest jako pierwszy element tablicy zniżek o jedynym polu \"CENA_NORMALNY\"\n\n" + securityNoticeString)
    public List<Map<String, Object>> getCennik(@Parameter(description = "idSeansu do sprawdzenia")@PathVariable int idSeansu,
                                                @AuthenticationPrincipal UserDetails userDetails)
    {
        authService.verifyStaff(userDetails);
        return ticketService.getCennik(idSeansu);
    }
    @GetMapping("/getSeanseForFilm/{id}")
    @Operation(summary = "Zwraca określoną parametrem iloscRekodow seansów dla filmu o podanym parametrem id", description = "Załącza dane z widoku v_AktywneSeanse z przedziału <zaczynajacOd, zaczynajacOd+iloscRekordow> \n\n" + securityNoticeString)
    public List<Map<String, Object>> getSeanseForFilm(@PathVariable int id,
                                                      @Parameter(description = "Od którego seansu zacząć.") @RequestParam(defaultValue = "0") int zaczynajacOd,
                                                      @Parameter(description = "Ilość seansów do zwrócenia. \n\n <b>-1 oznacza wszystkie możliwe</b>") @RequestParam(defaultValue = "-1") int iloscRekordow,
                                                      @AuthenticationPrincipal UserDetails userDetails)
    {
        authService.verifyStaff(userDetails);
        return seansRepository.findXForMovie(BigDecimal.valueOf(id),zaczynajacOd, iloscRekordow);
    }
    @PostMapping("/sellTicket")
    @Operation(summary = "Dokonuje sprzedaży biletu", description = "Tworzy nowe zamówienie i bilety na podane dane.\n\nW Request Body można opcjonalnie zawrzeć listę obiektów typu discountedTicketsDTO (Zobacz: discountedTicketsDTO) i/lub typu snacksDTO (Zobacz: snacksDTO)\n\nAby zobaczyć co dokładnie zwraca - zobacz: newOrderDTO.\n\n" + securityNoticeString)
    public TicketService.newOrderDTO buyTicket(@Parameter(description = "Id seansu na który chcesz kupić") @RequestParam BigDecimal seansId,
                                               @Parameter(description = "Karta/Gotówka") @RequestParam String paymentType,
                                               @Parameter(description = "Email klienta jeśli chcemy mu podpiąć pod jego konto. Opcjonalne") @RequestParam(required = false) String klientEmail,
                                               @Parameter(description = "Ile biletów normalnych. Musi być podana. Może być 0") @RequestParam BigDecimal normalTicketsAmount,
                                               @RequestBody(required = false) SellTicketRequest requestBody,
                                               @AuthenticationPrincipal UserDetails userDetails)
    {
        authService.verifyStaff(userDetails);
        List<TicketService.discountedTicketsDTO> discountedTickets;
        List<TicketService.snacksDTO> snacks;
        if(requestBody != null)
        {
            discountedTickets = requestBody.getDiscountedTickets();
            snacks = requestBody.getSnacks();
        }
        else
        {
            discountedTickets=null;
            snacks=null;
        }
        BigDecimal klientId;
        if(klientEmail != null)
        {
            klientId = BigDecimal.valueOf(authService.getClientIdFromEmail(klientEmail));
        }
        else {
            klientId = null;
        }
        TicketService.newOrderDTO newOrder = ticketService.sellTickets(BigDecimal.valueOf(authService.getStaffId(userDetails)), klientId, seansId, normalTicketsAmount, paymentType, discountedTickets, snacks);
        return newOrder;
    }
    @Data
    public static class SellTicketRequest {
        private List<TicketService.discountedTicketsDTO> discountedTickets;
        private List<TicketService.snacksDTO> snacks;
    }

}
