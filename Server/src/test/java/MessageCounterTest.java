import org.junit.Test;
import server.MessageBrowser;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageCounterTest {

    @Test
    public void getMessageCountOnQueue(){
        MessageBrowser messageCounter = new MessageBrowser("tcp://localhost:61616");

        List<Integer> list = messageCounter.getMessageCountOnQueue();
        assertNotNull(list);

        for(Integer queueSize : list){
            System.err.println(queueSize);
        }

    }

}
