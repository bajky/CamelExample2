package server.counter;

import org.apache.activemq.ActiveMQQueueBrowser;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Session;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageBrowserEngine extends ConnectableComponent {

    private static final int ACKNOWLEDGE_MODE = Session.CLIENT_ACKNOWLEDGE;
    private static final boolean TRANSACTIONAL = false;

    private final Logger logger = Logger.getLogger(this.getClass());

    public MessageBrowserEngine(String aciveMQURL) {
        super(aciveMQURL);
    }

    //detect whether is message on antoher queue
    public boolean isMessageOnAnotherQueue(String messageID, String... queues) {
        for (String queue : queues) {
            if (isMessageOnQueue(queue, messageID)) {
                return true;
            }
        }
        return true;
    }

    //detect whether is message on concrete queue
    public boolean isMessageOnQueue(String queueName, String messageID) {
        ActiveMQQueue activeMQQueue = getQueueByname(queueName);

        try {
            ActiveMQQueueBrowser browser = (ActiveMQQueueBrowser) getActiveMQSession().createBrowser(activeMQQueue, "JMSMessageID = '" + messageID + "'");
            Enumeration enumeration = browser.getEnumeration();

            return (Collections.list(enumeration).size() > 0);

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }

    //find queue by name
    public ActiveMQQueue getQueueByname(String queueName) {

        ActiveMQQueue activeMQQueue = null;
        try {
            Set<ActiveMQQueue> queues = getActiveMQConnection().getDestinationSource().getQueues();
            Iterator<ActiveMQQueue> queueIterator = queues.iterator();

            while (queueIterator.hasNext()) {
                activeMQQueue = queueIterator.next();
                if (activeMQQueue.getQueueName().equals(queueName)) {
                    break;
                }
            }
            return activeMQQueue;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    //return count of messages on specific Queue
    public Integer getMessageCountOnQueue(String queueName) {

        try {
            Set<ActiveMQQueue> queues = getActiveMQConnection().getDestinationSource().getQueues();
            Iterator<ActiveMQQueue> queueIterator = queues.iterator();

            ActiveMQQueue activeMQQueue = null;
            while (queueIterator.hasNext()) {

                activeMQQueue = queueIterator.next(); // get next activeMQ queue
                String comaparedQueueName = activeMQQueue.getQueueName();
                if (comaparedQueueName.equals(queueName)) {
                    break;
                }
                logger.debug(queueName);
            }

            ActiveMQQueueBrowser queueBrowser = (ActiveMQQueueBrowser) getActiveMQSession().createBrowser(activeMQQueue);//create queueBrowser

            Enumeration enumeration = queueBrowser.getEnumeration();
            int size = Collections.list(enumeration).size();

            return size;

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NullPointerException activeMQNullException) {
            System.err.println("ExceptionMessage " + activeMQNullException.getMessage());
            activeMQNullException.printStackTrace();
        }

        return null;
    }
}
