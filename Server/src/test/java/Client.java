import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import server.engineimplementation.MessageBrowserEngine;

import javax.jms.*;
import java.util.Iterator;
import java.util.Set;

public class Client {

    private static final int ACK_MODE = Session.AUTO_ACKNOWLEDGE;
    private static final String CLIENT_QUEUE_NAME = "server.receiver";

    private Logger logger = Logger.getLogger(Client.class);
    private Connection connection;
    private Session session;
    private ActiveMQMessageProducer producer;
    private MessageBrowserEngine messageBrowserEngine;

    private boolean transacted = false;
    private TextMessage message;

    public Client(String activeMQUrl) {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQUrl);

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(transacted, ACK_MODE);
            messageBrowserEngine = new MessageBrowserEngine(activeMQUrl);
            ActiveMQQueue activeMQQueue = messageBrowserEngine.getQueueByname(Server.CLIENT_QUEUE_NAME);

            producer = (ActiveMQMessageProducer) session.createProducer(activeMQQueue);

        } catch (JMSException e) {
            logger.debug("Exception in " + this.getClass().getName());
            e.printStackTrace();
        }
    }


    public void stopConnection() {

        try {
            session.close();
            connection.close();
            messageBrowserEngine.closeConnection();


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

    public TextMessage getTextMessage(){
        if(message instanceof TextMessage){
            return message;
        }

        return null;
    }

}