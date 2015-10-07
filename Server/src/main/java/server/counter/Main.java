package server.counter;

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
        boolean lastIncMessDeQ = messageCounterImplementation.isLastIncMessDeQ(200);
        logger.debug(lastIncMessDeQ);
    }
}
