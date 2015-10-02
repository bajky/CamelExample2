package server.client;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import server.routes.ServerRoute;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

/**
 * Created by Bajky on 13.9.2015.
 */
public class Client {

    public Client(){

    }

    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("spring/camel-context.xml");
        try {
            ConnectionFactory connFact = new ActiveMQConnectionFactory("tcp://0.0.0.0:61616");
            CamelContext camelContext = SpringCamelContext.springCamelContext(appContext, false);
            Connection connection = connFact.createConnection();

            connection.start();
            camelContext.start();

            ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
            producerTemplate.sendBody(ServerRoute.ENDPOINT_TEST_QUEUE, "hello World");

            connection.stop();
            camelContext.stop();
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
