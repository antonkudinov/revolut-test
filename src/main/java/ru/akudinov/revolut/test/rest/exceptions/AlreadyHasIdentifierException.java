package ru.akudinov.revolut.test.rest.exceptions;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class AlreadyHasIdentifierException extends ApplicationException {

    public AlreadyHasIdentifierException() {
        super("Entity already has identifier", BAD_REQUEST);
    }

}
