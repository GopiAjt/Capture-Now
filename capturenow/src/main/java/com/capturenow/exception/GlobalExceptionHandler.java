package com.capturenow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleMediaTypeNotSupportedException(org.springframework.web.HttpMediaTypeNotSupportedException e) {
        return new ResponseEntity<>("The request Content-Type is not supported. This endpoint requires 'multipart/form-data'. Please update your request headers and body.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException e) {
        return new ResponseEntity<>("The request is not a multipart request or the file size is too large. Please ensure you are using 'multipart/form-data'.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return new ResponseEntity<>("An internal error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
