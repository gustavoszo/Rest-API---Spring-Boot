package com.minhapi.parkapi.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.minhapi.parkapi.exception.CodigoUniqueViolationException;
import com.minhapi.parkapi.exception.CpfUniqueViolationException;
import com.minhapi.parkapi.exception.EntityNotFoundException;
import com.minhapi.parkapi.exception.ErrorPasswordException;
import com.minhapi.parkapi.exception.UsernameUniqueViolationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice // Espécie de ouvinte, captura as exceções e trata com os métodos adcionados na classe
public class ApiExceptionHandler {
    
    @ExceptionHandler(AccessDeniedException.class) // Registra a exceção para a classe captura-la
    public ResponseEntity<ErrorMessage> accessDeniedException(AccessDeniedException ex, 
                                                                HttpServletRequest request) {
        log.error("Api error - ", ex);                                                                  
        return ResponseEntity
               .status(HttpStatus.FORBIDDEN)
               .contentType(MediaType.APPLICATION_JSON) // Forçar a resposta como um application JSON
               .body(new ErrorMessage(request, HttpStatus.FORBIDDEN, ex.getMessage()));                                                         
                                                                    
    }
    
    @ExceptionHandler(EntityNotFoundException.class) // Registra a exceção para a classe captura-la
    public ResponseEntity<ErrorMessage> entityNotFoundException(RuntimeException ex, 
                                                                HttpServletRequest request) {
        log.error("Api error - ", ex);                                                                  
        return ResponseEntity
               .status(HttpStatus.NOT_FOUND)
               .contentType(MediaType.APPLICATION_JSON) // Forçar a resposta como um application JSON
               .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));                                                         
                                                                    
    }
    
    @ExceptionHandler(ErrorPasswordException.class)
    public ResponseEntity<ErrorMessage> errorPasswordException(RuntimeException ex, 
                                                                HttpServletRequest request) {
        log.error("Api error - ", ex);                                                                  
        return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .contentType(MediaType.APPLICATION_JSON) // Forçar a resposta como um application JSON
               .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, ex.getMessage()));                                                         
                                                                    
    }
    
    @ExceptionHandler({UsernameUniqueViolationException.class, CpfUniqueViolationException.class, CodigoUniqueViolationException.class})
    public ResponseEntity<ErrorMessage> uniqueViolationException(RuntimeException ex, 
                                                                HttpServletRequest request) {

        log.error("Api error - ", ex);                                                                  
        return ResponseEntity
               .status(HttpStatus.CONFLICT) // Status 409
               .contentType(MediaType.APPLICATION_JSON) // Forçar a resposta como um application JSON
               .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));                                                         
                                                                    
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidException(MethodArgumentNotValidException ex, 
                                                                        HttpServletRequest request, 
                                                                        BindingResult result) {

        log.error("Api error - ", ex);                                                                  
        return ResponseEntity
               .status(HttpStatus.UNPROCESSABLE_ENTITY) // Status 422; Não processa o objeto enviado pelo cliente, não contém os campos com a forma esperada
               .contentType(MediaType.APPLICATION_JSON) // Forçar a resposta como um application JSON
               .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_ENTITY, "Campo(s) inválido(s)", result));                                                         
                                                                    
    }

}
