package ru.akudinov.revolut.test.rest.exceptions;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class EntityNotFoundException extends ApplicationException {

    public EntityNotFoundException() {
        super("Entity not found", NOT_FOUND);
    }
}
