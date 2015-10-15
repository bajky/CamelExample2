package server.engineimplementation;

import org.apache.activemq.ActiveMQQueueBrowser;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.TextMessage;
import java.util.*;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageBrowser extends ConnectableComponent {


    private final Logger logger = Logger.getLogger(this.getClass());

    public MessageBrowser(String aciveMQURL) {
        super(aciveMQURL);
    }

    //detect whether is message on antoher queue
    public boolean isMessageOnAnotherQueue(String messageID, String... queues) {
        for (String queue : queues) {
            if (isMessageOnQueue(messageID, queue)) {
                return true;
            }
        }
        return false;
    }

    //detect whether is message on concrete queue
    public boolean isMessageOnQueue(String messageID, String queueName) {
        ActiveMQQueue activeMQQueue = getQueueByname(queueName);

        try {
            ActiveMQQueueBrowser browser = (ActiveMQQueueBrowser) getActiveMQSession().createBrowser(activeMQQueue, "JMSMessageID = '" + messageID + "'");
            Enumeration enumeration = browser.getEnumeration();


            int size = Collections.list(enumeration).size();
            return (size > 0);

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
                    return activeMQQueue;
                }
            }

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


    public Message getMessageByText(Set<Message> messages, String text) {
        Iterator<Message> iterator = messages.iterator();

        while (iterator.hasNext()) {
            Message nextMessage = iterator.next();
            try {
                if (nextMessage instanceof TextMessage) {
                    boolean equals = ((TextMessage) nextMessage).getText().equals(text);
                    if (equals) {
                        System.err.println(nextMessage);
                        return nextMessage;
                    }
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Set<Message> getMessagesOnQueue(String queueName) {
        ActiveMQQueue queue = getQueueByname(queueName);
        Enumeration enumeration = null;
        try {
            ActiveMQQueueBrowser activeMQBrowser = (ActiveMQQueueBrowser) getActiveMQSession().createBrowser(queue);
            enumeration = activeMQBrowser.getEnumeration();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return new HashSet<Message>(Collections.list(enumeration));

    }


}
