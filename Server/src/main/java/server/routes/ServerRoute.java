package server.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import server.Messanger;


@Component
public class ServerRoute extends RouteBuilder {

    public static final String ENDPOINT_TEST_QUEUE = "jms:queue:test.queue";

    @Override
    public void configure() throws Exception {//define route
        from(ENDPOINT_TEST_QUEUE)
                .log(LoggingLevel.DEBUG, ServerRoute.class.getSimpleName(), "message arrived in the route - Body = ${bodyAs(String)}")
                .bean(Messanger.class)
                .to("bean:messageWrapper")
                .log(LoggingLevel.DEBUG, ServerRoute.class.getSimpleName(), "message transformed to - Body = ${body}")
                .end();
    }
}
