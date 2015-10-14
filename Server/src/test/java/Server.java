import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

/**
 * @author David david.bajko@senacor.com
 */
public class Server {
    public static final String CLIENT_QUEUE_NAME = "server.receiver";
    public static final String DLQ = "dlq";

    private Connection connection;
    private String acttiveMQURL;
    private String messageType;


    private ActiveMQSession session;
    private ActiveMQMessageProducer messageProducer;
    private BrokerService broker;
    private ActiveMQMessageConsumer consumer;
    private String messageID;


    public Server(String activeMQURL, String messageType) {
        this.acttiveMQURL = activeMQURL;
        this.messageType = messageType;

        broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(false);

        try {
            broker.addConnector(activeMQURL);
            broker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        createConnection();
    }

    private void createConnection() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(acttiveMQURL);
        activeMQConnectionFactory.setDispatchAsync(false);
        try {
            connection = activeMQConnectionFactory.createConnection();
            connection.start();

            session = (ActiveMQSession) connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            ActiveMQQueue testQueue = (ActiveMQQueue) session.createQueue(CLIENT_QUEUE_NAME);
            ActiveMQQueue dlqQueue = (ActiveMQQueue) session.createQueue(DLQ);

            messageProducer = (ActiveMQMessageProducer) session.createProducer(dlqQueue);

            consumer = (ActiveMQMessageConsumer) session.createConsumer(testQueue, "JMSType = '" + messageType + "'");
            consumer.setMessageListener(new MessageListerForServer());

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    public void stopConnection() {
        try {

            session.close();
            session.dispose();
            connection.stop();
            connection.close();
            broker.stop();

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MessageListerForServer implements MessageListener {

        public void onMessage(Message message) {
            try {
                messageID = message.getJMSMessageID();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public ActiveMQSession getSession() {
        return session;
    }

    public String getRecievedMessageID() {
        return messageID;
    }

}
