package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.Data;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.ProduktRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.SeansRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.ZnizkaRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.AuthService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.RepertuarService;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services.TicketService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservation")
public class ReservationSystemController {

    private final RepertuarService repertuarService;
    private final ProduktRepository produktRepository;
    private final SeansRepository seansRepository;
    private final AuthService authService;
    private static final String securityNoticeString = "Do logowania korzystamy z <b>tokenów JWT.</b> \n\n Wymagane jest wcześniejsze zalogowanie klienta. Token do zapytania dołączamy jako header \"Authorization\" w formacie \"Bearer TOKEN\"";
    private final TicketService ticketService;
    private final ZnizkaRepository znizkaRepository;

    public ReservationSystemController(RepertuarService repertuarService, ProduktRepository produktRepository, SeansRepository seansRepository, AuthService authService, TicketService ticketService, ZnizkaRepository znizkaRepository) {
        this.repertuarService = repertuarService;
        this.produktRepository = produktRepository;
        this.seansRepository = seansRepository;
        this.authService = authService;
        this.ticketService = ticketService;
        this.znizkaRepository = znizkaRepository;
    }
    @GetMapping("/public/przekaski")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    @Operation(summary = "Zwraca wszystkie dostępne przekąski")
    public List<Map<String, Object>> listaPrzekasek() {
        return produktRepository.groupFilterId(produktRepository.findAll());
    }

    @GetMapping("/public/getRepertuar")
    @Operation(summary = "Zwraca pełen repertuar kina", description = "Zwraca wszystkie filmy w kinie posiadające aktualne seanse.\n\n Dodatkowo dołącza: dane reżysera, dane aktorów oraz dane z widoku v_AktywneSeanse dla maks 5 najświeższych seansów z każdego filmu.")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public List<Map<String, Object>> getRepertuar() {
        return repertuarService.getRepertuar();
    }

    @GetMapping("/public/getSeanseForFilm/{id}")
    @Operation(summary = "Zwraca określoną parametrem iloscRekodow seansów dla filmu o podanym parametrem id", description = "Załącza dane z widoku v_AktywneSeanse z przedziału <zaczynajacOd, zaczynajacOd+iloscRekordow>")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public List<Map<String, Object>> getSeanseForFilm(@PathVariable int id,
                                                      @Parameter(description = "Od którego seansu zacząć.") @RequestParam(defaultValue = "0") int zaczynajacOd,
                                                      @Parameter(description = "Ilość seansów do zwrócenia. \n\n <b>-1 oznacza wszystkie możliwe</b>") @RequestParam(defaultValue = "-1") int iloscRekordow)
    {
        return seansRepository.findXForMovie(BigDecimal.valueOf(id),zaczynajacOd, iloscRekordow);
    }
    @GetMapping("/getSeansInfo/{idSeansu}")
    @Operation(summary = "Zwraca szczegóły o konkretnym seansie", description = "Załącza dane z tabeli Seans: dataCzas, nazwa Filmu, czas trwania Filmu, nazwa Sali, nazwa Typu Seansu, językSeansu, dubbingCzyNapisy oraz cenaSpecjalna\n\n" + securityNoticeString)
    public Map<String, Object> getSeansInfo(@Parameter(description = "Id seansu do sprawdzenia.") @PathVariable int idSeansu)
    {
        return repertuarService.getInfoSeans(BigDecimal.valueOf(idSeansu));
    }
    @GetMapping("/public/getZnizki")
    @Operation(summary = "Zwraca wszystkie dostępne zniżki", description = "Zwraca całą zawartość tabeli RodzajeZniżek.")
    @SecurityRequirements //Info dla Swaggera: Nie wymaga Tokena
    public List<Map<String, Object>> getZnizki()
    {
        return znizkaRepository.findAll();
    }

    @GetMapping("/getCennik/{idSeansu}")
    @Operation(summary = "Zwraca cene danego seansu rozpisując wszystkie dostępne zniżki", description = "Zwraca całą zawartość tabeli RodzajeZniżek dodając do każdej pole CENA określające dokładną cene tego seansu używając tej zniżki. \n\nCena standardowa tego seansu (dla biletu normalnego) dodawana jest jako pierwszy element tablicy zniżek o jedynym polu \"CENA_NORMALNY\" \n\n" + securityNoticeString)
    public List<Map<String, Object>> getCennik(@Parameter(description = "idSeansu do sprawdzenia")@PathVariable int idSeansu,
                                               @AuthenticationPrincipal UserDetails userDetails)
    {
        authService.verifyClient(userDetails);
        return ticketService.getCennik(idSeansu);
    }

    @GetMapping("/getBiletyKlienta")
    @Operation(summary = "Zwraca wszystkie bilety zalogowanego klienta", description = "Załącza dane z widoku v_RezerwacjeKlienta\n\n" + securityNoticeString)
    public List<Map<String, Object>> getClientTickets(@AuthenticationPrincipal UserDetails userDetails)
    {
        int ClientId = authService.getClientId(userDetails);
        return ticketService.getUserTickets(ClientId);
    }
    @PostMapping("/buyTicket")
    @Operation(summary = "Dokonuje zakupu biletu", description = "Tworzy nowe zamówienie i bilety na podane dane.\n\nW Request Body można opcjonalnie zawrzeć listę obiektów typu discountedTicketsDTO (Zobacz: discountedTicketsDTO)\n\n Aby zobaczyć co dokładnie zwraca - zobacz: newOrderDTO. \n\n" + securityNoticeString)
    public TicketService.newOrderDTO buyTicket(@Parameter(description = "Id seansu na który chcesz kupić") @RequestParam BigDecimal seansId,
                                               @Parameter(description = "Przelew (jak płatność przelewem)/Kasa (jak płatność przy kasie)") @RequestParam String paymentType,
                                               @Parameter(description = "Ile biletów normalnych. Musi być podana. Może być 0") @RequestParam BigDecimal normalTicketsAmount,
                                               @RequestBody(required = false) BuyTicketRequest requestBody,
                                               @AuthenticationPrincipal UserDetails userDetails)
    {
        List<TicketService.discountedTicketsDTO> discountedTickets;
        if(requestBody != null)
        {
            discountedTickets = requestBody.getDiscountedTickets();
        }
        else
        {
            discountedTickets=null;
        }
        TicketService.newOrderDTO newOrder = ticketService.sellTickets(null, BigDecimal.valueOf(authService.getClientId(userDetails)), seansId, normalTicketsAmount, paymentType, discountedTickets, null);
        return newOrder;
    }

    @Data
    public static class BuyTicketRequest {
        private List<TicketService.discountedTicketsDTO> discountedTickets;
    }
}
