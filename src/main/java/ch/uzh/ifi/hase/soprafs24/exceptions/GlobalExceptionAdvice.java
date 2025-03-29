package ch.uzh.ifi.hase.soprafs24.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
// (annotations = RestController.class)
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

  private final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

  @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
  protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
    String bodyOfResponse = "This should be application specific";
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(TransactionSystemException.class)
  public ResponseStatusException handleTransactionSystemException(Exception ex, HttpServletRequest request) {
    log.error("Request: {} raised {}", request.getRequestURL(), ex);
    return new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
  }

  // Keep this one disable for all testing purposes -> it shows more detail with
  // this one disabled
  @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
  public ResponseStatusException handleException(Exception ex) {
    log.error("Default Exception Handler -> caught:", ex);
    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
  }

@ExceptionHandler(ResponseStatusException.class)
public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
    log.warn("Handling ResponseStatusException - Status: {}, Reason: {}", ex.getStatus(), ex.getReason());

    Map<String, Object> errorBody = new HashMap<>();
    errorBody.put("timestamp", java.time.LocalDateTime.now());
    errorBody.put("status", ex.getStatus().value());
    errorBody.put("error", ex.getStatus().getReasonPhrase());
    errorBody.put("message", ex.getReason());
    errorBody.put("path", request.getRequestURI());

    return new ResponseEntity<>(errorBody, ex.getStatus());
}

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, 
      HttpHeaders headers, 
      HttpStatus status, 
      WebRequest request) {
      
      Map<String, String> errors = new HashMap<>();
      ex.getBindingResult().getAllErrors().forEach((error) -> {
          String fieldName = ((FieldError) error).getField();
          String errorMessage = error.getDefaultMessage();
          errors.put(fieldName, errorMessage);
      });

      return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }


}