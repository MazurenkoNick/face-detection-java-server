package com.face.faceanalyzer.advice;

import com.face.faceanalyzer.data.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneralException(Exception ex) {
        log.error("Handle error: ", ex);
        return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
