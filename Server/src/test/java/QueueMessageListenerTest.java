import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.engineimplementation.QueueMessageListener;

import javax.jms.Message;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author David david.bajko@senacor.com
 */
public class QueueMessageListenerTest {
    private static final String MESSAGE_BROKER_URL = "tcp://localhost:61616";
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
    public void getIncommingMessagesTest(){
        QueueMessageListener queueMessageListener = new QueueMessageListener(MESSAGE_BROKER_URL, Server.CLIENT_QUEUE_NAME);

        Client client = new Client(MESSAGE_BROKER_URL);



        client.stopConnection();
        Set<Message> incommingMessages = queueMessageListener.getIncommingMessages(2000);
        assertEquals(2, incommingMessages.size());
    }
}
