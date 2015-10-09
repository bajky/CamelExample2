package server.engineimplementation;

import org.apache.log4j.Logger;

/**
 * @author David david.bajko@senacor.com
 */
public class Main {
    private static final String ACTIVEMQ_URL = "tcp://0.0.0.0:61616";
    private static final String QUEUE = "dead";
    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        MessageCounterImplementation messageCounterImplementation = new MessageCounterImplementation(ACTIVEMQ_URL, QUEUE);
        server.client.Client.sendMessage(null);

        boolean wasDequeued = messageCounterImplementation.messageWasDequeued("test.queue", "dead", "heldlo Worlds");
        logger.debug(wasDequeued + "asdassssssssssssssssssssssssssdasd");
    }
}
