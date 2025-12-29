package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public void registerClient(String imie, String nazwisko, String email, String haslo)
    {
        String sql = "INSERT INTO KLIENT (imie, nazwisko, haslo, email, poziomlojalnosciowy) VALUES (?,?,?,?,0);";
        jdbcTemplate.update(sql, imie, nazwisko, encoder.encode(haslo), email);
    }
}
