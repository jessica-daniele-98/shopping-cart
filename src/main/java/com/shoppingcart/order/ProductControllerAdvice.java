package com.shoppingcart.order;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = ProductController.class)
class ProductControllerAdvice {

  @ExceptionHandler(DuplicateKeyException.class)
  ProblemDetail handleDuplicateKeyException() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Product already exists");
  }

  @ExceptionHandler
  ProblemDetail handleNotFoundException(ProductNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  ProblemDetail handleException() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Error during request");
  }

}
