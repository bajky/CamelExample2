package server;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Bajky on 8.9.2015.
 */
public class Server {

    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("spring/camel-context.xml");
        try {
            CamelContext camelContext = SpringCamelContext.springCamelContext(appContext, false);
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
