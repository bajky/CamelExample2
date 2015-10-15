import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.engineimplementation.MessageBrowser;
import server.engineimplementation.QueueMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author David david.bajko@senacor.com
 */
public class QueueMessageListenerTest {
    private static final String MESSAGE_BROKER_URL = "tcp://localhost:61616?jms.prefetchPolicy.queuePrefetch=2";
    private static Server server;
    private Logger logger = Logger.getLogger(BrowserEngineTest.class);

    @BeforeClass
    public static void createConnection() throws Exception {
        server = new Server(MESSAGE_BROKER_URL, "error");
    }

    @AfterClass
    public static void closeConnection() {
        server.stopConnection();
    }

    @Test
    public void getIncommingMessagesTest() {
        QueueMessageListener queueMessageListener = new QueueMessageListener(MESSAGE_BROKER_URL, Server.CLIENT_QUEUE_NAME);
        Client client = new Client(MESSAGE_BROKER_URL);
        client.sendMessage("messae", "error");
        client.sendMessage("message2", "error");

        MessageBrowser messageBrowser = new MessageBrowser(MESSAGE_BROKER_URL);
        int messageCountOnQueue = messageBrowser.getMessageCountOnQueue(Server.CLIENT_QUEUE_NAME);


        assertEquals(2, messageCountOnQueue);
        queueMessageListener.closeConnection();

        client.stopConnection();
        queueMessageListener.closeConnection();
        messageBrowser.closeConnection();
    }

    @Test
    public void onlyErrorMessageIsConsumed() throws JMSException {
        QueueMessageListener queueMessageListener = new QueueMessageListener(MESSAGE_BROKER_URL, Server.CLIENT_QUEUE_NAME);

        Client client = new Client(MESSAGE_BROKER_URL);
        client.sendMessage("message1", "error");
        client.sendMessage("message2", "normal");
        client.sendMessage("message3", "error");
        client.sendMessage("message4", "error");
        queueMessageListener.getIncommingMessages(2000);
        client.stopConnection();
        queueMessageListener.closeConnection();

    }
}
