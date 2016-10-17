package ru.akudinov.revolut.test.rest;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.jetty.JettyTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;
import ru.akudinov.revolut.test.rest.model.Account;
import ru.akudinov.revolut.test.rest.model.Payment;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.akudinov.revolut.test.rest.WebApp.createMoxyJsonResolver;


public class WebAppTest extends JerseyTest {


    @Test
    public void testNotFound() {
        testEntityNotFound("accounts/1000");
        testEntityNotFound("payments/1000");
    }

    @Test
    public void testCreate() {

        //first account
        Response response = createAccount(1);
        assertEquals(SC_OK, response.getStatus());
        assertEquals(text("createAccountAnswer1.json"), response.readEntity(String.class));

        //second account
        response = createAccount(2);
        assertEquals(SC_OK, response.getStatus());
        assertEquals(text("createAccountAnswer2.json"), response.readEntity(String.class));


        //payment
        response = createPayment(entity(text("createPayment1.json"), APPLICATION_JSON_TYPE));

        assertEquals(SC_OK, response.getStatus());

        final Payment actual = response.readEntity(Payment.class);
        assertEquals((Long) 1L, actual.getId());
        assertEquals(1L, actual.getWithdrawalAccountNumber());
        assertEquals(2L, actual.getDepositAccountNumber());
        assertEquals(new BigDecimal("101.12"), actual.getAmount());
        assertNotNull(actual.getDate());

        Account account1 = findAccount(1);
        Account account2 = findAccount(2);

        assertEqualsBigDecimal(new BigDecimal("998.65"), account1.getBalance());
        assertEqualsBigDecimal(new BigDecimal("111.12"), account2.getBalance());

    }

    private Response createAccount(int n) {
        Invocation post = target("accounts").request()
                .buildPost(entity(text(String.format("createAccount%s.json", n)), APPLICATION_JSON_TYPE));

        return post.invoke();
    }

    private Account findAccount(int id) {
        return target("accounts/" + id).request().buildGet().invoke().readEntity(Account.class);
    }


    private void assertEqualsBigDecimal(BigDecimal v1, BigDecimal v2) {
        int i = v1.compareTo(v2);
        assertEquals(0, i);
    }


    private Response createPayment(Entity<String> entity) {
        return target("payments").request().buildPost(entity).invoke();
    }


    private void testEntityNotFound(String path) {
        final Response response = target(path).request().buildGet().invoke();
        assertEquals(SC_NOT_FOUND, response.getStatus());
        assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());
        assertEquals(text("entityNotFound.json"), response.readEntity(String.class));
    }

    private String text(String filename) {
        try {
            return Files.readAllLines(Paths.get(ClassLoader.getSystemResource(filename).toURI()))
                    .stream()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return String.format("file %s not found", filename);
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new JettyTestContainerFactory();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MoxyJsonFeature.class);
        config.register(createMoxyJsonResolver());
        super.configureClient(config);
    }

    @Override
    protected Application configure() {
        return WebApp.createJerseyConfig();
    }

}