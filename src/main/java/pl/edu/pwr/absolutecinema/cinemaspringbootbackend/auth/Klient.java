package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Klient")
public class Klient {
    @jakarta.persistence.Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long KlientID;

    @Setter
    @Getter
    @Column(unique = true, nullable = false)
    private String email;

    @Setter
    @Getter
    @Column(nullable = false)
    private String haslo;

    @Setter
    @Getter
    @Column(nullable = false)
    private String imie;

    @Setter
    @Getter
    @Column(nullable = false)
    private String nazwisko;

    @Setter
    @Getter
    @Column(nullable = false)
    private int poziomlojalnosciowy;
}
