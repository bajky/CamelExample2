package server.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class ServerRoute extends RouteBuilder{

    @Override
    public void configure() throws Exception {
        from("activemq:queue:test.queue")
                .log("nieco")
                .to("bean:componentImpl");
    }
}
