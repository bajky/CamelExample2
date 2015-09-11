import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import server.routes.ServerRoute;

import java.util.concurrent.TimeUnit;

/**
 * @author David david.bajko@senacor.com
 */

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring-test-context.xml")
public class ClientTest {

    private static final String REQUEST = "test message";

    @Produce(uri = ServerRoute.ENDPOINT_TEST_QUEUE)
    ProducerTemplate producerTemplate;

    @EndpointInject(uri ="mock:dlq")
    MockEndpoint mockDlqEndpoint;

    @EndpointInject(uri = "mock:messenger")
    MockEndpoint mockMassenger;

    @Test
    public void messageResponseTest() throws Exception {

//        mockMassenger.setExpectedCount(1);
        mockDlqEndpoint.setExpectedCount(1);

        producerTemplate.sendBody(REQUEST);
//        mockDlqEndpoint.assertIsSatisfied();

//        TimeUnit.SECONDS.sleep(4);
        mockDlqEndpoint.assertIsSatisfied();

    }
}
