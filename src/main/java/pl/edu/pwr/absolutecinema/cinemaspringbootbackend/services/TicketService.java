package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.stereotype.Service;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeansRepository seansRepository;
    private final ZnizkaRepository znizkaRepository;
    private final ZamowienieRepository zamowienieRepository;
    private final ProduktRepository produktRepository;

    public TicketService(TicketRepository ticketRepository, SeansRepository seansRepository, ZnizkaRepository znizkaRepository, ZamowienieRepository zamowienieRepository, ProduktRepository produktRepository) {
        this.ticketRepository = ticketRepository;
        this.seansRepository = seansRepository;
        this.znizkaRepository = znizkaRepository;
        this.zamowienieRepository = zamowienieRepository;
        this.produktRepository = produktRepository;
    }
    public List<Map<String, Object>> getUserTickets(int userId) {
        List<Map<String, Object>> bilety = new ArrayList<>();
        BigDecimal klientId = BigDecimal.valueOf(userId);
        bilety.addAll(ticketRepository.findAllByKlientId(klientId));
        return bilety;
    }
    public BigDecimal calculateCena(Map<String, Object> seans, Map<String, Object> znizka)
    {
        BigDecimal cenaKoncowa;
        if(seans.get("CENASPECJALNA")!=null)
        {
            cenaKoncowa = (BigDecimal) seans.get("CENASPECJALNA");
        }
        else
        {
            BigDecimal typSeansuId =  (BigDecimal) seans.get("TYPSEANSUID");
            cenaKoncowa = (BigDecimal) seansRepository.findTypeById(typSeansuId).get("CENASTANDARDOWA");
        }
        if(znizka!=null)
        {
            BigDecimal procentZnizki = (BigDecimal) znizka.get("PROCENTZNIZKI");
            BigDecimal kwotaZnizki = procentZnizki.multiply(cenaKoncowa).divide(new BigDecimal(100));
            cenaKoncowa = cenaKoncowa.subtract(kwotaZnizki);
        }
        return cenaKoncowa.setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal getCena(BigDecimal seansId, BigDecimal znizkaId)
    {
        Map<String, Object> znizka;
        if(znizkaId != null)
        {
            znizka = znizkaRepository.findById(znizkaId);
        }
        else
        {
            znizka = null;
        }
        Map<String, Object> seans = seansRepository.findById(seansId);
        return calculateCena(seans, znizka);
    }
    public List<Map<String, Object>> getCennik(int seansId) {
        List<Map<String, Object>> znizki = znizkaRepository.findAll();
        Map<String,Object> seans = seansRepository.findById(BigDecimal.valueOf(seansId));

        for (int i=0; i<znizki.size();i++)
        {
            System.out.println(i + " : ");
            System.out.println(znizki);
            Map<String, Object> znizka = znizki.get(i);
            System.out.println(znizki.get(i));
            BigDecimal cena = calculateCena(seans, znizki.get(i));
            znizka.put("CENA", cena);
            znizki.set(i, znizka);
        }
        Map<String,Object> normalTicket = new HashMap<>();
        normalTicket.put("CENA_NORMALNY", calculateCena(seans, null));
        znizki.add(0, normalTicket);
        return znizki;
    }
    public newOrderDTO sellTickets(BigDecimal pracownikId, BigDecimal klientId, BigDecimal seansId, BigDecimal normalTicketsAmount, String paymentType, List<discountedTicketsDTO> discountedTickets, List<snacksDTO> snacks)
    {
        BigDecimal ticketId = null;
        BigDecimal cenaKoncowa = BigDecimal.ZERO;
        newOrderDTO newOrder = new newOrderDTO();
        newOrder.bilety = new ArrayList<>();
        if(pracownikId!=null)
        {
            //EMPLOYEE MODE
            newOrder.zamowienieId = zamowienieRepository.createOrder(klientId, pracownikId, "Zakupione", paymentType);
            //Przekaski
            if(snacks!=null) {
                for (int i = 0; i < snacks.size(); i++) {
                    produktRepository.createPozycjaOrder(newOrder.zamowienieId, snacks.get(i).produktId, snacks.get(i).amount);
                    cenaKoncowa = cenaKoncowa.add(produktRepository.getCena(snacks.get(i).produktId, snacks.get(i).amount));
                }
            }
        }
        else if(klientId!=null){
            //CUSTOMER MODE

            //utworzZamowienie
            if(paymentType.equals("Przelew"))
            {
                newOrder.zamowienieId = zamowienieRepository.createOrder(klientId, null, "Zakupione", "Przelew");
            }
            else if(paymentType.equals("Kasa"))
            {
                newOrder.zamowienieId = zamowienieRepository.createOrder(klientId, null, "Zarezerwowane", "Kasa");
            }
            else {
                throw new IllegalArgumentException("Nie poprawny typ płatności");
            }
        }
        else
        {
            throw new IllegalArgumentException("Konieczne KlientId lub PracownikId");
        }


        //Bilet Normalny
        BigDecimal cenaNormalny = getCena(seansId, null);
        for(int i=0; i<normalTicketsAmount.intValue();i++)
        {
            BigDecimal newTicket = ticketRepository.createTicket(seansId, newOrder.zamowienieId, null);
            newOrder.bilety.add(newTicket);
            cenaKoncowa = cenaKoncowa.add(cenaNormalny);
        }
        //Bilet Znizki
        if(discountedTickets!=null) {
            for (int i = 0; i < discountedTickets.size(); i++) {
                BigDecimal cenaZaTyp = getCena(seansId, discountedTickets.get(i).znizkaId);
                for (int ii = 0; ii < discountedTickets.get(i).amount.intValue(); ii++) {
                    BigDecimal newTicket = ticketRepository.createTicket(seansId, newOrder.zamowienieId, discountedTickets.get(i).znizkaId);
                    newOrder.bilety.add(newTicket);
                    cenaKoncowa = cenaKoncowa.add(cenaZaTyp);
                }
            }
        }
        zamowienieRepository.updateCena(newOrder.zamowienieId, cenaKoncowa.setScale(2, RoundingMode.HALF_UP));
        return newOrder;
    }
    @Data
    @AllArgsConstructor
    public static class discountedTicketsDTO {
        @Getter
        @Setter
        @Schema(example = "1")
        private BigDecimal znizkaId;
        @Getter
        @Setter
        @Schema(example = "2")
        private BigDecimal amount;
    }
    @Data
    @AllArgsConstructor
    public static class snacksDTO {
        @Getter
        @Setter
        @Schema(example = "1")
        private BigDecimal produktId;
        @Getter
        @Setter
        @Schema(example = "2")
        private BigDecimal amount;
    }
    @Data
    @NoArgsConstructor
    public class newOrderDTO {
        @Getter
        @Setter
        @Schema(example = "1", description = "Zwraca id utworzonego zamówienia")
        private BigDecimal zamowienieId;
        @Getter
        @Setter
        @Schema(example = "[4324, 4325]", description = "Zwraca liste id biletów utworzonych w ramach zamówienia")
        private List<BigDecimal> bilety;
    }
}
