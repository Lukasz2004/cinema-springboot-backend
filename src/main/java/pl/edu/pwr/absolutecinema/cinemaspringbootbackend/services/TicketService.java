package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services;

import org.springframework.stereotype.Service;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final SeansRepository seansRepository;
    private final ZnizkaRepository znizkaRepository;

    public TicketService(TicketRepository ticketRepository, SeansRepository seansRepository, ZnizkaRepository znizkaRepository) {
        this.ticketRepository = ticketRepository;
        this.seansRepository = seansRepository;
        this.znizkaRepository = znizkaRepository;
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
        if(znizka.get("PROCENTZNIZKI")!=null)
        {
            BigDecimal procentZnizki = (BigDecimal) znizka.get("PROCENTZNIZKI");
            BigDecimal kwotaZnizki = procentZnizki.multiply(cenaKoncowa).divide(new BigDecimal(100));
            cenaKoncowa = cenaKoncowa.subtract(kwotaZnizki);
        }
        return cenaKoncowa;
    }
    public BigDecimal getCena(int seansId, int znizkaId)
    {
        Map<String, Object> znizka = znizkaRepository.findById(BigDecimal.valueOf(znizkaId));
        Map<String, Object> seans = seansRepository.findById(BigDecimal.valueOf(seansId));
        return calculateCena(seans, znizka);
    }
    public List<Map<String, Object>> getCennik(int seansId) {
        List<Map<String, Object>> znizki = znizkaRepository.findAll();
        Map<String,Object> seans = seansRepository.findById(BigDecimal.valueOf(seansId));
        for (int i=0; i<znizki.size();i++)
        {
            Map<String, Object> znizka = znizki.get(i);
            BigDecimal cena = calculateCena(seans, znizki.get(i));
            znizka.put("CENA", cena);
            znizki.set(i, znizka);
        }
        return znizki;
    }
}
