import org.junit.Before;
import org.junit.Test;
import server.client.Client;
import server.counter.MessageListenerForCount;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageListenerForCountTest {

    @Before
    public void setup(){
        MessageListenerForCount messageListenerForCount = new MessageListenerForCount( "dead", "tcp://localhost:61616");
    }

    @Test
    public void incommingMesasgesCountTest() throws InterruptedException {


    }
}
