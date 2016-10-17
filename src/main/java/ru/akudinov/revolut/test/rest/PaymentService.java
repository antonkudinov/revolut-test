package ru.akudinov.revolut.test.rest;

import org.glassfish.hk2.extras.interception.Intercepted;
import ru.akudinov.revolut.test.rest.data.PaymentRepository;
import ru.akudinov.revolut.test.rest.model.Payment;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("/payments")
@Intercepted
public class PaymentService {
    @Inject
    private PaymentRepository repository;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Payment create(Payment payment) {
        return repository.create(payment);
    }

    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Payment read(@PathParam("id") long id) {
        return repository.read(id);
    }
}
