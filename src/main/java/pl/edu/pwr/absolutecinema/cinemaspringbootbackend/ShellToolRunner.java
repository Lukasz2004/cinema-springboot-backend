package pl.edu.pwr.absolutecinema.cinemaspringbootbackend;


import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.security.crypto.password.PasswordEncoder;

@ShellComponent
public class ShellToolRunner {

    private final PasswordEncoder passwordEncoder;

    public ShellToolRunner(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public String stringFormatter(String string) {
        return "\n[ABSOLUTE-CINEMA-SHELL] " + string + "\n";
    }
    @ShellMethod(key = "szyfruj", value = "Szyfruje podane hasło")
    public String encrypt(String password) {
        return stringFormatter("Zaszyfrowane haslo: " + passwordEncoder.encode(password));
    }
}