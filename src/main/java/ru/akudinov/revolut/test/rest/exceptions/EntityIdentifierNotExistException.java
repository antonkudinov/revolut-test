package ru.akudinov.revolut.test.rest.exceptions;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class EntityIdentifierNotExistException extends ApplicationException {

    public EntityIdentifierNotExistException() {
        super("Entity not has identifier", BAD_REQUEST);
    }

}
