package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(UserRepository userRepository, EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Klient
        Optional<Klient> klient = userRepository.findByEmail(username);
        if (klient.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(klient.get().getEmail())
                    .password(klient.get().getHaslo())
                    .roles("Klient")
                    .build();
        }

        // Pracownik
        Optional<Pracownik> pracownik = employeeRepository.findByEmail(username);
        if (pracownik.isPresent()) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(pracownik.get().getEmail())
                    .password(pracownik.get().getHaslo())
                    .roles("Pracownik")
                    .build();
        }

        //Nie znaleziono
        throw new UsernameNotFoundException("Nie znaleziono użytkownika o emailu: " + username);
    }
}