package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.services;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
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
}
