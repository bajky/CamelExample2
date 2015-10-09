import org.apache.activemq.*;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.engineimplementation.MessageCounterImplementation;

import javax.jms.*;
import javax.jms.Message;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @author David david.bajko@senacor.com
 */
public class BrowserEngineTest {
    private static final String MESSAGE_BROKER_URL = "tcp://localhost:61616";

    private static final String ACRIVEMQ_DLQ = "dlq";
    private static final String ACRIVEMQ_TEST_QUEUE = "test";

    private ActiveMQConnection activeMQConnection;
    private ActiveMQSession activeMQSession;
    private ActiveMQQueueBrowser activeMQQueueBrowser;

    private static Server server;

    @BeforeClass
    public static void createConnection() throws Exception {
        server = new Server(MESSAGE_BROKER_URL);
    }

    @AfterClass
    public static void closeConnection() {
        server.stopConnection();
    }

    private void setupConnection() throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(MESSAGE_BROKER_URL);

        activeMQConnection = (ActiveMQConnection) activeMQConnectionFactory.createConnection();
        activeMQConnection.start();
        activeMQSession = (ActiveMQSession) activeMQConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        MessageProducer producer = activeMQSession.createProducer(null);

        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        ActiveMQQueue activeMQQueue = (ActiveMQQueue) activeMQSession.createQueue("test.queue");
        ActiveMQQueue activeMQDLQ = (ActiveMQQueue) activeMQSession.createQueue("dlq");
    }

    private void createServer() throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(MESSAGE_BROKER_URL);

        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        ActiveMQSession session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        ActiveMQQueue testQueue = (ActiveMQQueue) session.createQueue(ACRIVEMQ_TEST_QUEUE);
        ActiveMQQueue dlqQueue = (ActiveMQQueue) session.createQueue(ACRIVEMQ_DLQ);

        ActiveMQMessageProducer messageProducer = (ActiveMQMessageProducer) session.createProducer(null);
        ActiveMQMessageConsumer consumer = (ActiveMQMessageConsumer) session.createConsumer(testQueue);

        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {

            }
        });


    }


    @Test
    public void messageProccessAccessTest() {
        MessageCounterImplementation messageCounterImplementation = new MessageCounterImplementation(MESSAGE_BROKER_URL, Server.CLIENT_QUEUE_NAME);

        Client client = new Client(MESSAGE_BROKER_URL);
        client.sendMessage("test message");
        boolean b = messageCounterImplementation.messageWasDequeued(Server.CLIENT_QUEUE_NAME, Server.DLQ, "test message");
        System.err.println(b);
        assertTrue(b);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.stopConnection();

    }


}
