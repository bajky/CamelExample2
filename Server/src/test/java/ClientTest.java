import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.Messanger;
import server.routes.ServerRoute;

/**
 * @author David david.bajko@senacor.com
 */

public class ClientTest extends CamelTestSupport {

    private static final String REQUEST = "test message";

    private static final String DIRECT_ROUTE = "direct:route";


    @Mock
    Messanger messanger;

    @InjectMocks
    ServerRoute serverRoute = new ServerRoute();

    @Produce(uri = DIRECT_ROUTE)
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:dlq")
    private MockEndpoint mockDlqEndpoint;

    @EndpointInject(uri = "mock:messenger")
    private MockEndpoint mockMassenger;

    private void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void messageResponseTest() throws Exception {
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint(ServerRoute.ENDPOINT_FOR_DLQ)
                        .skipSendToOriginalEndpoint()
                        .to("mock:dlq");
            }
        });


        mockDlqEndpoint.setExpectedCount(1);

        producerTemplate.sendBody(REQUEST);

        mockDlqEndpoint.assertIsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        setup();
        replaceRouteFromWith(ServerRoute.ENDPOINT_TEST_QUEUE_ID, DIRECT_ROUTE);
        return new ServerRoute();
    }
}
