package com.shoppingcart.order;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = OrderController.class)
class OrderControllerAdvice {

  @ExceptionHandler(DuplicateKeyException.class)
  ProblemDetail handleDuplicateKeyException() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Order already exists");
  }

  @ExceptionHandler(exception = {OrderNotFoundException.class, ItemOrderNotFoundException.class})
  ProblemDetail handleNotFoundException(Exception e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  ProblemDetail handleException() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error during request");
  }
}
