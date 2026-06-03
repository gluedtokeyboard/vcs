package io.github.gluedtokeyboard.vcs.api.common.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        return createResponse(
                HttpStatus.NOT_FOUND,
                new ErrorResponse(ex.getMessage())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        return createResponse(
                HttpStatus.BAD_REQUEST,
                new ErrorResponse(ex.getMessage())
        );
    }

    /**
     * Creates a response with the given status and body.
     *
     * @param status the response status
     * @param body the response body
     * @return a response with the given status and body
     */
    private static ResponseEntity<ErrorResponse> createResponse(HttpStatus status, ErrorResponse body) {
        return ResponseEntity.status(status).body(body);
    }
}
