import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.engineimplementation.MessageBrowserEngine;
import server.engineimplementation.MessageCounterImplementation;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author David david.bajko@senacor.com
 */
public class BrowserEngineTest {
    public static final String MESSAGE_BROKER_URL = "tcp://localhost:61616";

    private static Server server;
    private Logger logger = Logger.getLogger(BrowserEngineTest.class);
    @BeforeClass
    public static void createConnection() throws Exception {
        server = new Server(MESSAGE_BROKER_URL);
    }

    @AfterClass
    public static void closeConnection() {
        server.stopConnection();
    }


    @Test
    public void messagewasDequeuedTest() {
        Client client = new Client(MESSAGE_BROKER_URL);
        MessageCounterImplementation messageCounterImplementation = new MessageCounterImplementation(MESSAGE_BROKER_URL, Server.CLIENT_QUEUE_NAME);

        client.sendMessage("test message", "error");


        boolean b = messageCounterImplementation.messageWasDequeued(Server.CLIENT_QUEUE_NAME, Server.DLQ, "test message");
        try {
            System.err.println(client.getTextMessage().getJMSType());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        assertTrue(b);
        messageCounterImplementation.closeConnections();

        client.stopConnection();

    }

    @Test
    public void getMessageByTextTest() throws JMSException {
        String correctMessage = "correct message";
        String incorectMessage = "incorectMessage";

        TextMessage message1 = server.getSession().createTextMessage(correctMessage);
        TextMessage message2 = server.getSession().createTextMessage(incorectMessage);

        Set<Message> messages = new HashSet<Message>();
        messages.add(message1);
        messages.add(message2);

        MessageBrowserEngine messageBrowserEngine = new MessageBrowserEngine(MESSAGE_BROKER_URL);
        TextMessage result = (TextMessage) messageBrowserEngine.getMessageByText(messages, correctMessage);

        assertEquals(correctMessage, result.getText());

        messageBrowserEngine.closeConnection();
    }

    @Test
    public void getQueueByNameTest() throws JMSException {


        MessageBrowserEngine messageBrowserEngine = new MessageBrowserEngine(MESSAGE_BROKER_URL);
        ActiveMQQueue resultQueue = messageBrowserEngine.getQueueByname(Server.CLIENT_QUEUE_NAME);

        assertEquals(Server.CLIENT_QUEUE_NAME, resultQueue.getQueueName());
        messageBrowserEngine.closeConnection();
    }

    @Test
    public void isMessageOnQueueTest() throws JMSException, InterruptedException {
        Client client = new Client(MESSAGE_BROKER_URL);

        MessageBrowserEngine messageBrowserEngine = new MessageBrowserEngine(MESSAGE_BROKER_URL);
        client.sendMessage("asdasd", "error");

        TimeUnit.SECONDS.sleep(2);
        System.err.println("error z testu " + server.getRecievedMessageID());
        assertTrue(messageBrowserEngine.isMessageOnQueue(server.getRecievedMessageID(), Server.CLIENT_QUEUE_NAME));

        messageBrowserEngine.closeConnection();

        client.stopConnection();
    }


}
