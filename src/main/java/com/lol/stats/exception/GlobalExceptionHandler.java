package com.lol.stats.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignExceptions(FeignException ex) {
        if (ex.status() == 429) {
            System.err.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Zbyt wiele żądań w krótkim czasie");
        } else if (ex.status() == 404){
            System.err.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404");
        } else {
            System.err.println(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd, sprawdź logi");
        }
    }
}
