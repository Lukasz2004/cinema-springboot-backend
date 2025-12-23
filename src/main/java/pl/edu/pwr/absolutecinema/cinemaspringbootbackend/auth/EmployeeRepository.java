package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Pracownik, Long> {
    Optional<Pracownik> findByEmail(String email);
}
