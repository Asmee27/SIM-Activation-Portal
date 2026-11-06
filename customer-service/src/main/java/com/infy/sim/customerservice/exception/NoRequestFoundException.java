package com.infy.sim.customerservice.exception;

/** No activation request exists for the email; returned as 404. */
public class NoRequestFoundException extends RuntimeException {

    public NoRequestFoundException(String message) {
        super(message);
    }
}
