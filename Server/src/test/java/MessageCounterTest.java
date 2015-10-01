import org.junit.Test;
import server.MessageBrowser;

import static org.junit.Assert.assertNotNull;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageCounterTest {

    private static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    @Test
    public void getMessageCountOnQueue() {
        MessageBrowser messageCounter = new MessageBrowser(ACTIVEMQ_URL);

        Integer size = messageCounter.getMessageCountOnQueue("xxx");
        assertNotNull(size);

        System.err.println(size);

    }

    @Test
    public void getCountOfDequeuedMsgTest(){
        MessageBrowser browser = new MessageBrowser(ACTIVEMQ_URL);
        browser.getCountOfDequeuedMsg("dead");
    }

}
