package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class AuthService {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder encoder;
    public AuthService(JdbcTemplate jdbcTemplate, PasswordEncoder encoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.encoder = encoder;
    }
    public boolean verifyClient(UserDetails userDetails)
    {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak dostępu: Nie zalogowano");
        }
        boolean isKlient = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Klient"));
        if(!isKlient)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak dostępu: Nie poprawny typ użytkownika");
        }
        return true;
    }
    public boolean verifyStaff(UserDetails userDetails)
    {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak dostępu: Nie zalogowano");
        }
        boolean isPracownik = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Pracownik"));
        if(!isPracownik)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Brak dostępu: Nie poprawny typ użytkownika");
        }
        return true;
    }
    public int getClientId(UserDetails userDetails)
    {
        verifyClient(userDetails);
        return getClientIdFromEmail(userDetails.getUsername());
    }
    public int getClientIdFromEmail(String email)
    {
        try {
            String sql = "SELECT klientID FROM Klient WHERE email = ?";
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class, email);

            if (id == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd danych: Znaleziono użytkownika, ale brak ID.");
            }
            return id;

        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono profilu klienta w bazie danych.");
        }
    }
    public int getStaffId(UserDetails userDetails)
    {
        verifyStaff(userDetails);
        return getStaffIdFromEmail(userDetails.getUsername());
    }
    public int getStaffIdFromEmail(String email)
    {
        try {
            String sql = "SELECT pracownikID FROM Pracownik WHERE email = ?";
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class, email);

            if (id == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Błąd danych: Znaleziono użytkownika, ale brak ID.");
            }
            return id;

        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono profilu pracownika w bazie danych.");
        }

    }
    public void registerClient(String imie, String nazwisko, String email, String haslo)
    {
        String sql = "INSERT INTO KLIENT (imie, nazwisko, haslo, email, poziomlojalnosciowy) VALUES (?,?,?,?,0);";
        jdbcTemplate.update(sql, imie, nazwisko, encoder.encode(haslo), email);
    }
}
