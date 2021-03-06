package ru.akudinov.revolut.test.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.servlet.ServletContainer;
import ru.akudinov.revolut.test.rest.data.AccountRepository;
import ru.akudinov.revolut.test.rest.data.PaymentRepository;
import ru.akudinov.revolut.test.rest.exceptions.JsonExceptionMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.Servlet;
import javax.ws.rs.ext.ContextResolver;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static org.eclipse.persistence.jaxb.JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME;

public class WebApp {

    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        Servlet servlet = new ServletContainer(createJerseyConfig());

        ServletHolder servletHolder = new ServletHolder("jersey", servlet);
        servletHolder.setInitOrder(0);

        context.addServlet(servletHolder, "/*");

        Server server = new Server(8081);
        server.setHandler(context);
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    static ResourceConfig createJerseyConfig() {
        return new ResourceConfig()
                .packages("ru.akudinov.revolut.test.rest", "ru.akudinov.revolut.test.rest.data")
                .register(MoxyJsonFeature.class)
                .register(createMoxyJsonResolver())
                .register(JsonExceptionMapper.class)
                .register(EntityManagerBinder.create())
                .register(new AbstractContainerLifecycleListener() {
                    @Override
                    public void onStartup(Container container) {
                        final ServiceLocator locator = container.getApplicationHandler().getServiceLocator();
                        if (locator.getBestDescriptor(BuilderHelper.createContractFilter(AccountRepository.class.getName())) == null) {
                            ServiceLocatorUtilities.addClasses(locator, AccountRepository.class);
                        }
                        if (locator.getBestDescriptor(BuilderHelper.createContractFilter(PaymentRepository.class.getName())) == null) {
                            ServiceLocatorUtilities.addClasses(locator, PaymentRepository.class);
                        }
                    }
                })
                .register(AccountService.class);
    }

    static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        Map<String, String> namespacePrefixMapper = new HashMap<>(1);
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");

        final MoxyJsonConfig config = new MoxyJsonConfig();
        config.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
        config.setIncludeRoot(true);
        config.setValueWrapper("value");
        config.property(JSON_WRAPPER_AS_ARRAY_NAME, TRUE);
        config.setAttributePrefix("");


        return config.resolver();
    }

    private static class EntityManagerBinder {
        private static AbstractBinder create() {
            return new AbstractBinder() {
                @Override
                protected void configure() {
                    bindFactory(new Factory<EntityManager>() {

                        private final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("TEST");

                        @Override
                        public EntityManager provide() {
                            return EMF.createEntityManager();
                        }

                        @Override
                        public void dispose(EntityManager em) {
                            em.close();
                        }
                    }).to(EntityManager.class).in(RequestScoped.class);
                }
            };
        }
    }


}
