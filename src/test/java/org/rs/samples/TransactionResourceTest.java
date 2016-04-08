package org.rs.samples;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TransactionResourceTest {

    private WebTarget target;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(
                        TransactionConfig.class, TransactionResource.class,
                        Transaction.class, TransactionRepository.class,
                        TransactionExceptions.class, InvalidTransactionException.class);
    }

    @ArquillianResource
    private URL base;

    @Before
    public void setUp() throws MalformedURLException {
        Client client = ClientBuilder.newClient();
        target = client.target(URI.create(new URL(base, "transactionservice").toExternalForm()));
        target.register(Transaction.class);
    }

	@Test @InSequence(1)
    public void testPostAndGet() {
    	JsonObject model_1 = Json.createObjectBuilder()
    			.add("type", "cars")
    			.add("amount", 7500.50)
    			.build();
    
        target.path("/transaction").request().post(Entity.json(model_1));

    	JsonObject model_2 = Json.createObjectBuilder()
    			.add("type", "shopping")
    			.add("amount", 500.99)
    			.build();
    	target.path("/transaction").request().post(Entity.json(model_2));

        JsonArray list = target.path("/transaction").request().get(JsonArray.class);
        assertEquals(2, list.size());

        assertEquals("cars", list.getJsonObject(0).getString("type"));
        assertEquals(7500.5, list.getJsonObject(0).getJsonNumber("amount").doubleValue(), 0.001);

        assertEquals("shopping", list.getJsonObject(1).getString("type"));
        assertEquals(500.99, list.getJsonObject(1).getJsonNumber("amount").doubleValue(), 0.001);
    }
}