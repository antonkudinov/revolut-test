package ru.akudinov.revolut.test.rest.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.PrintWriter;
import java.io.StringWriter;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.xml.bind.annotation.XmlAccessType.NONE;

@Provider
public class JsonExceptionMapper implements ExceptionMapper<Exception> {


    public static final int _1_KB = 1024;

    @Override
    public Response toResponse(Exception e) {
        final Error entity = new Error();
        final Response.Status status;
        if (e instanceof ApplicationException) {
            entity.message = e.getMessage();
            status = ((ApplicationException) e).getStatus();
        } else {
            final StringWriter message = new StringWriter(_1_KB);
            PrintWriter out = new PrintWriter(message);
            e.printStackTrace(out);
            out.flush();

            entity.message = message.getBuffer();
            status = INTERNAL_SERVER_ERROR;
        }

        return Response
                .status(status)
                .entity(entity)
                .type(APPLICATION_JSON_TYPE)
                .build();
    }

    @XmlRootElement
    @XmlAccessorType(NONE)
    private static final class Error {
        @XmlElement
        private CharSequence message;
    }
}