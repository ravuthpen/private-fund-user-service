package com.fund.server.accountservice.exception;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Resource not found");
        problem.setType(URI.create("https://api.fundtrnsactionhub.com/problems/not-found"));
        return problem;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                exception.getStatus(),
                exception.getMessage()
        );

        problem.setTitle(exception.getErrorCode());
        problem.setType(URI.create("https://api.fundtrnsactionhub.com/problems/"
        +exception.getErrorCode().toLowerCase().replace("-","-")));
        problem.setProperty("errorCode", exception.getErrorCode());
        return problem;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ServerWebInputException.class})
    public ProblemDetail handleValidation(Exception exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        problem.setTitle("Validation error");
        problem.setProperty("errors", List.of(exception.getMessage()));
        return problem;
    }
    
    
}
