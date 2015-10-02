package server.counter;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageListenerForCount implements MessageListener {
    private static int ackMode = Session.AUTO_ACKNOWLEDGE;

    private final String activeMQurl;
    private static boolean TRANSACTED = false;

    private Session session;
    private ActiveMQConnection connection;
    private MessageProducer messageProducer;
    private Logger logger = Logger.getLogger(MessageListenerForCount.class);
    private String queueName;

    private int messageCount = 0;

    public MessageListenerForCount(String queueName, String activeMQurl) {
        this.activeMQurl = activeMQurl;
        this.queueName = queueName;
    }

    public void startCountOn() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQurl);

        try {
            connection = (ActiveMQConnection) connectionFactory.createConnection(); // creating connection
            connection.start(); // starting connection

            session = connection.createSession(TRANSACTED, ackMode); //creating session
            ActiveMQQueue queue = getQueueByname(queueName);

            this.messageProducer = this.session.createProducer(null);
            this.messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            MessageConsumer consumer = this.session.createConsumer(queue);
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            logger.debug("Error in " + this.getClass().getName() + " at starting");
            e.printStackTrace();
        }
    }

    private ActiveMQQueue getQueueByname(String name) {

        ActiveMQQueue activeMQQueue = null;
        try {
            Set<ActiveMQQueue> queues = connection.getDestinationSource().getQueues();
            Iterator<ActiveMQQueue> queueIterator = queues.iterator();

            while (queueIterator.hasNext()) {
                activeMQQueue = queueIterator.next();
                if (activeMQQueue.getQueueName().equals(name)) {
                    break;
                }
            }
            return activeMQQueue;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onMessage(Message message) {
        messageCount++;
        System.err.println(messageCount);
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void stopCount() {
        try {
            session.close();
            connection.stop();
        } catch (JMSException e) {
            logger.debug("exception in " + this.getClass().getName());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MessageListenerForCount messageListenerForCount = new MessageListenerForCount("dead","tcp://localhost:61616");
        messageListenerForCount.startCountOn();
    }

    private class Route extends RouteBuilder {
        private String DIRECT_ROUTE = "direct:route";

        @Override
        public void configure() throws Exception {
            interceptSendToEndpoint("jms:queue:dead")
                    .log(LoggingLevel.DEBUG, "Message was redirectet to" + DIRECT_ROUTE)
                    .to(DIRECT_ROUTE);
        }
    }


}
