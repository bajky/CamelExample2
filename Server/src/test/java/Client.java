import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.Iterator;
import java.util.Set;

public class Client {

    private static final int ACK_MODE = Session.CLIENT_ACKNOWLEDGE;
    private static final String CLIENT_QUEUE_NAME = "server.receiver";
    private static final String TEST_MESSAGE = "test message";

    private Logger logger = Logger.getLogger(Client.class);
    private Connection connection;
    private Session session;
    private ActiveMQMessageProducer producer;

    private boolean transacted = false;

    public Client(String activeMQUrl) {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQUrl);

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(transacted, ACK_MODE);

            ActiveMQQueue producerQueue = (ActiveMQQueue) session.createQueue(CLIENT_QUEUE_NAME);
            producer = (ActiveMQMessageProducer) session.createProducer(producerQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText(TEST_MESSAGE);
            producer.send(txtMessage);
        } catch (JMSException e) {
            logger.debug("Exception in " + this.getClass().getName());
            e.printStackTrace();
        }
    }


    public void stopConnection() {

        try {
            session.close();
            connection.close();


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String messageText) {

        try {

            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText(messageText);
            producer.send(txtMessage);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(TextMessage txtMessage) {
        try {
            producer.send(txtMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMessages(Set<Message> messageSet) {
        Iterator<Message> messageIterator = messageSet.iterator();
        try {
            while (messageIterator.hasNext()) {

                producer.send(messageIterator.next());
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}