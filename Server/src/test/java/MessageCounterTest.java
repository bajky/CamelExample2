import org.junit.Test;
import server.MessageBrowser;

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageCounterTest {

//    <plugins>
//    <statisticsBrokerPlugin/>
//    </plugins>
    private static final String ACTIVEMQ_URL = "tcp://localhost:61616";
    @Test
    public void getMessageCountOnQueue() {
        MessageBrowser messageCounter = new MessageBrowser(ACTIVEMQ_URL);

        Integer size = messageCounter.getMessageCountOnQueue("dead");
        assertNotNull(size);
        System.err.println(size);

    }

    @Test
    public void getCountOfDequeuedMsgTest() throws Exception{
        MessageBrowser browser = new MessageBrowser(ACTIVEMQ_URL);
        int countOfDequeuedMsg = browser.getCountOfDequeuedMsg("test.queue");

        assertEquals(0, countOfDequeuedMsg);
    }
    @Test
    public void messageListenerTest() throws JMSException {
        MessageBrowser browser = new MessageBrowser(ACTIVEMQ_URL);
        browser.messageListener("aaaaa");
    }

}
