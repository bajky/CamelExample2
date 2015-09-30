package server.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import server.Messanger;


@Component
public class ServerRoute extends RouteBuilder {

    public static final String INTERCEPT_ENDPOIT = "log:queue:intercept.endpoint";

    public static final String ENDPOINT_TEST_QUEUE = "jms:queue:test.queue";
    public static final String ENDPOINT_FOR_DLQ = "jms:queue:dead";
    public static final String ENDPOINT_TEST_QUEUE_ID = "serverRoute";


    @Override
    public void configure() throws Exception {//define route


        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.WARN, "Exception was invoke. Pleas fix it")
                .to(ENDPOINT_FOR_DLQ);


        from(ENDPOINT_TEST_QUEUE)
                .routeId(ENDPOINT_TEST_QUEUE_ID)
                .log(LoggingLevel.DEBUG, ServerRoute.class.getSimpleName(), "message arrived in the route - Body = ${bodyAs(String)}")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        throw new Exception();
                    }
                })
                .bean(Messanger.class)
                .log(LoggingLevel.DEBUG, ServerRoute.class.getSimpleName(), "message transformed to - Body = ${body}")
                .end();
    }
}
