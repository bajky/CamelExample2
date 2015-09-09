package client;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Bajky on 8.9.2015.
 */
public class Client {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("app-context.xml");
        try {
            CamelContext camelContext = SpringCamelContext.springCamelContext(applicationContext, false);

            ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
            String response = (String)producerTemplate.requestBody("activemq:queue:test.queue", "nasd");

            System.out.println(response);
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
