package com.infy.sim.customerservice.exception;

/** Business validation failure; returned to the UI as 400 with the message. */
public class InvalidDetailsException extends RuntimeException {

    public InvalidDetailsException(String message) {
        super(message);
    }
}
