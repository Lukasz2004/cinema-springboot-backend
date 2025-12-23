package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Klient, Long> {
    Optional<Klient> findByEmail(String email);
}
