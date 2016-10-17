package ru.akudinov.revolut.test.rest;

import org.glassfish.hk2.extras.interception.Intercepted;
import ru.akudinov.revolut.test.rest.data.AccountRepository;
import ru.akudinov.revolut.test.rest.model.Account;

import javax.inject.Inject;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("/accounts")
@Intercepted
public class AccountService {

    @Inject
    private AccountRepository repository;


    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Account create(Account account) {
        return repository.create(account);
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Account update(Account account) {
        return repository.update(account);
    }


    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Account read(@PathParam("id") long id) {
        return repository.read(id);
    }
}
