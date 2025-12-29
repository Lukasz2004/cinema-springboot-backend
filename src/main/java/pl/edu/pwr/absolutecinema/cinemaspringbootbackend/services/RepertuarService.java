package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services;

import org.springframework.stereotype.Service;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.AktorRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.FilmRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.RezyserRepository;
import pl.edu.pwr.absolutecinema.cinemaspringbootbackend.repositories.SeansRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RepertuarService {
    private final FilmRepository filmRepository;
    private final RezyserRepository rezyserRepository;
    private final AktorRepository aktorRepository;
    private final SeansRepository seansRepository;
    public RepertuarService(FilmRepository filmRepository, RezyserRepository rezyserRepository, AktorRepository aktorRepository, SeansRepository seansRepository) {
        this.filmRepository = filmRepository;
        this.rezyserRepository = rezyserRepository;
        this.aktorRepository = aktorRepository;
        this.seansRepository = seansRepository;
    }
    public List<Map<String, Object>> getRepertuar() {
        List<Map<String, Object>> repertuar = new ArrayList<>();
        repertuar.addAll(filmRepository.findAllActive());
        for(int i = 0; i < repertuar.size(); i++)
        {
            Map<String, Object> tempMap = repertuar.get(i);
            BigDecimal filmId = (BigDecimal) tempMap.get("FILMID");
            BigDecimal rezyserId = (BigDecimal) tempMap.get("REZYSERID");
            tempMap.put("REZYSER", rezyserRepository.filterId(rezyserRepository.findById(rezyserId)));
            List<Map<String, Object>> actors = aktorRepository.findAllForMovie(filmId);
            tempMap.put("AKTORZY", actors);
            List<Map<String, Object>> viewings = seansRepository.findXForMovie(filmId, 0,5);
            tempMap.put("SEANSE", viewings);

            tempMap.remove("REZYSERID");

            repertuar.set(i, tempMap);
        }
        return repertuar;
    }
    public List<Map<String, Object>> getRepertuarShort() {
        List<Map<String, Object>> repertuar = new ArrayList<>();
        repertuar.addAll(filmRepository.findAllActive());
        for(int i = 0; i < repertuar.size(); i++)
        {
            Map<String, Object> tempMap = repertuar.get(i);
            BigDecimal filmId = (BigDecimal) tempMap.get("FILMID");

            List<Map<String, Object>> languages = seansRepository.findAllLanguagesForMovie(filmId);
            tempMap.put("JEZYKI", languages);

            tempMap.remove("REZYSERID");

            repertuar.set(i, tempMap);
        }
        return repertuar;
    }
}
