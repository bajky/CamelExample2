import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import server.routes.ServerRoute;

import static org.junit.Assert.assertEquals;

/**
 * @author David david.bajko@senacor.com
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/camel-context.xml")
public class ClientTest {

    private static final String REQUEST = "test message";

    @Produce(uri = ServerRoute.ENDPOINT_TEST_QUEUE)
    ProducerTemplate producerTemplate;

    @Test
    public void messageResponseTest() throws Exception {
        String result = (String)producerTemplate.requestBody(REQUEST);
        assertEquals("This is mesasge: Messanger say: " + REQUEST, result);
    }
}
