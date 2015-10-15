import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import server.engineimplementation.MessageBrowser;

import javax.jms.*;
import java.util.Iterator;
import java.util.Set;

public class Client {

    private static final int ACK_MODE = Session.AUTO_ACKNOWLEDGE;

    private Logger logger = Logger.getLogger(Client.class);
    private Connection connection;
    private Session session;
    private ActiveMQMessageProducer producer;
    private MessageBrowser messageBrowser;

    private boolean transacted = false;
    private TextMessage message;

    public Client(String activeMQUrl) {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQUrl);

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(transacted, ACK_MODE);
            messageBrowser = new MessageBrowser(activeMQUrl);
            ActiveMQQueue activeMQQueue = messageBrowser.getQueueByname(Server.CLIENT_QUEUE_NAME);

            producer = (ActiveMQMessageProducer) session.createProducer(activeMQQueue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        } catch (JMSException e) {
            logger.debug("Exception in " + this.getClass().getName());
            e.printStackTrace();
        }
    }


    public void stopConnection() {

        try {
            session.close();
            connection.close();
            messageBrowser.closeConnection();


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String messageText, String messageType) {

        try {

            message= session.createTextMessage();
            message.setText(messageText);
            message.setJMSType(messageType);
            producer.send(message);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


}