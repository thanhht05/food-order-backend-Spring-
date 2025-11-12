package com.thanh.foodOrder.util.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.thanh.foodOrder.domain.ResResponse;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = {
            CommonException.class
    })
    public ResponseEntity<ResResponse<Object>> handleException(Exception exception) {
        ResResponse<Object> res = new ResResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage("Business logic error");

        res.setError(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResResponse<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        ResResponse<Object> res = new ResResponse<>();
        res.setError(exception.getBody().getDetail());
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());

        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

    }

}
