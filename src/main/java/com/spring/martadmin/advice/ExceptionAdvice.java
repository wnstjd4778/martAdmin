package com.spring.martadmin.advice;

import com.spring.martadmin.advice.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(DuplicateDataException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ErrorResponse handleDuplicateDataException(DuplicateDataException e) {
        return ErrorResponse.ErrorOf(403, e.getMessage());
    }

    @ExceptionHandler(SessionUnstableException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleSessionUnstableException(SessionUnstableException e) {
        return ErrorResponse.ErrorOf(401, e.getMessage());
    }

    @ExceptionHandler(OAuth2AuthenticationProcessingException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ErrorResponse handleOAuth2AuthenticationProcessingException (OAuth2AuthenticationProcessingException e) {
        return ErrorResponse.ErrorOf(403, e.getMessage());
    }

    @ExceptionHandler(SMSException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse handleSMSException(SMSException e) {
        return ErrorResponse.ErrorOf(500, e.getMessage());
    }

    @ExceptionHandler(NotFoundDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorResponse handleNotFoundException(NotFoundDataException e) {
        return ErrorResponse.ErrorOf(404, e.getMessage());
    }

}
