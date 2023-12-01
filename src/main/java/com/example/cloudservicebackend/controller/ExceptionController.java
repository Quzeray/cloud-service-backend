package com.example.cloudservicebackend.controller;

import com.example.cloudservicebackend.exception.*;
import com.example.cloudservicebackend.model.response.ErrorResponse;
import com.example.cloudservicebackend.model.response.LoginErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<LoginErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        final String errorMessage = "РќРµРІРµСЂРЅС‹Р№ Р»РѕРіРёРЅ РёР»Рё РїР°СЂРѕР»СЊ";
        LoginErrorResponse loginErrorResponse = LoginErrorResponse.builder()
                .id(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage())
                .email(new String[]{errorMessage})
                .password(new String[]{errorMessage})
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginErrorResponse);
    }

    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class})
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({BadLimitException.class, CreateFileException.class, FileNotExistsException.class,
            UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestsException(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({SearchFileException.class, DetailIdNotFoundException.class, UploadFileException.class,
                    DeleteFileException.class, DownloadFileException.class, RenameFileException.class})
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(CloudServiceException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
