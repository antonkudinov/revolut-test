package ru.akudinov.revolut.test.rest.exceptions;

import javax.ws.rs.core.Response;

public class ApplicationException extends RuntimeException {

    private final Response.Status status;

    public ApplicationException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }

}
