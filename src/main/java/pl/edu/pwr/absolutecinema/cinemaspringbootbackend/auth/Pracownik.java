package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Pracownik")
public class Pracownik {
    @jakarta.persistence.Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pracownikID;

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
    private String rolapracownika;


    @Setter
    @Getter
    @Column(nullable = false)
    private boolean czyaktywny;
}
