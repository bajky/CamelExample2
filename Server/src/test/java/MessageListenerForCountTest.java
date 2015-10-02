import org.junit.Test;
import server.counter.MessageListenerForCount;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageListenerForCountTest {

    @Test
    public void incommingMesasgesCountTest(){
        MessageListenerForCount messageListenerForCount = new MessageListenerForCount("dead");
        messageListenerForCount.startCountOn("tcp://localhost:61616");

    }
}
