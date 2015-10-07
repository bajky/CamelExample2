package server.counter;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQQueueBrowser;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageListenerForCount {
    private static int ackMode = Session.CLIENT_ACKNOWLEDGE;
    ActiveMQMessageConsumer acctiveMQConsumer;
    private final String activeMQurl;
    private static boolean TRANSACTED = false;

    private Session session;
    private ActiveMQConnection connection;
    private Logger logger = Logger.getLogger(MessageListenerForCount.class);
    private String queueName;

    public MessageListenerForCount(String activeMQurl, String queueName) {
        this.activeMQurl = activeMQurl;
        this.queueName = queueName;
        createConnection();
        setMessageListener();
    }

    private void createConnection() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQurl);


        try {

            connection = (ActiveMQConnection) connectionFactory.createConnection(); // creating connection
            connection.start(); // starting connection

            session = connection.createSession(TRANSACTED, ackMode); //creating session

        } catch (JMSException e) {
            logger.debug("Error in " + this.getClass().getName() + " at starting");
            e.printStackTrace();
        }
    }

    // return queue by string name
    private ActiveMQQueue getQueueByname(String name) {

        ActiveMQQueue activeMQQueue = null;
        try {
            Set<ActiveMQQueue> queues = connection.getDestinationSource().getQueues();
            Iterator<ActiveMQQueue> queueIterator = queues.iterator();

            while (queueIterator.hasNext()) {
                activeMQQueue = queueIterator.next();
                if (activeMQQueue.getQueueName().equals(name)) {
                    break;
                }
            }
            return activeMQQueue;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    //close connection and session
    public void closeConnection() {
        try {
            session.close();
            connection.stop();
            System.exit(0);
        } catch (JMSException e) {
            logger.debug("exception in " + this.getClass().getName());
            e.printStackTrace();
        }
    }


    //if message is not on another que, is On last queue
    public boolean isMessageOnAnotherQueue(String messageID, String... queues) {
        for (String queue : queues) {
            if (isMessageOnQueue(queue, messageID)) {
                return true;
            }
        }
        return true;
    }

    //set Listener of the consumer
    private void setMessageListener() {
        try {
            ActiveMQQueue activeMQQueue =  getQueueByname(queueName);

            acctiveMQConsumer = (ActiveMQMessageConsumer) session.createConsumer(activeMQQueue);
            acctiveMQConsumer.setMessageListener(new MessageReceiver());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    //find message with specific MessageID
    public boolean isMessageOnQueue(String queueName, String messageID) {
        ActiveMQQueue activeMQQueue = getQueueByname(queueName);

        try {
            ActiveMQQueueBrowser browser = (ActiveMQQueueBrowser) session.createBrowser(activeMQQueue, "JMSMessageID = 'ID:Davidko-50786-1443708845697-1:2:7:1:1'");
            Enumeration enumeration = browser.getEnumeration();

            return (Collections.list(enumeration).size() > 0);

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }

    //listener for messages
    private class MessageReceiver implements MessageListener {

        public void onMessage(Message message) {
            String messageID = null;



        }
    }

    //return count of messages on specific Queue
    public Integer getMessageCountOnQueue(String name) {

        try {
            Set<ActiveMQQueue> queues = connection.getDestinationSource().getQueues();

            Iterator<ActiveMQQueue> queueIterator = queues.iterator();

            ActiveMQQueue activeMQQueue = null;

            while (queueIterator.hasNext()) {

                activeMQQueue = queueIterator.next(); // get next activeMQ queue

                String queueName = activeMQQueue.getQueueName();
                if (queueName.equals(name)) {
                    break;
                }
                logger.debug(queueName);
            }

            ActiveMQQueueBrowser queueBrowser = (ActiveMQQueueBrowser) session.createBrowser(activeMQQueue);//create queueBrowser

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
