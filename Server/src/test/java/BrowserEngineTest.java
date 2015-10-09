import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import server.engineimplementation.MessageCounterImplementation;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * @author David david.bajko@senacor.com
 */
public class BrowserEngineTest {
    public static final String MESSAGE_BROKER_URL = "tcp://localhost:61616?jms.useAsyncSend=true&jms.dispatchAsync=true&wireFormat.maxInactivityDuration=-1";

    private static Server server;

    @BeforeClass
    public static void createConnection() throws Exception {
        server = new Server(MESSAGE_BROKER_URL);
    }

    @AfterClass
    public static void closeConnection() {
        server.stopConnection();
    }



    @Test
    public void messageProccessAccessTest() {
        MessageCounterImplementation messageCounterImplementation = new MessageCounterImplementation(MESSAGE_BROKER_URL, Server.CLIENT_QUEUE_NAME);

        Client client = new Client(MESSAGE_BROKER_URL);
        client.sendMessage("test message");
        boolean b = messageCounterImplementation.messageWasDequeued(Server.CLIENT_QUEUE_NAME, Server.DLQ, "test message");
        System.err.println(b);
        assertTrue(b);
        messageCounterImplementation.closeConnections();

        client.stopConnection();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
