package org.example.google.drive.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.google.drive.api.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.Instant;

@ControllerAdvice
@Slf4j
public class ExceptionHandler extends ResponseEntityExceptionHandler {
	@org.springframework.web.bind.annotation.ExceptionHandler(value = {IOException.class})
	protected ResponseEntity<ErrorResponse> handleIOException(final IOException ex, final WebRequest request) {
		return ResponseEntity.status(500)
							 .body(ErrorResponse.builder()
												.message(ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.build());
	}

	@org.springframework.web.bind.annotation.ExceptionHandler(value = {AuthorizationDeniedException.class})
	protected ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(final AuthorizationDeniedException ex, final WebRequest request) {
		return ResponseEntity.status(400)
							 .body(ErrorResponse.builder()
												.message( ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.build());
	}

	@org.springframework.web.bind.annotation.ExceptionHandler(value = {Exception.class})
	protected ResponseEntity<ErrorResponse> handleUnknownException(final Exception ex, final WebRequest request) {
		return ResponseEntity.status(500)
							 .body(ErrorResponse.builder()
												.message("Internal Server Error. " + ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.build());
	}
}
