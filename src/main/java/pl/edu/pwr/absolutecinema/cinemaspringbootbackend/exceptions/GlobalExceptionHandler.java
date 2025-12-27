package pl.edu.pwr.absolutecinema.cinemaspringbootbackend.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ErrorResponse handleDatabaseError(DataAccessException ex) {
        return new ErrorResponse("DATABASE_ERROR", "Wystąpił problem z bazą danych");
    }
}