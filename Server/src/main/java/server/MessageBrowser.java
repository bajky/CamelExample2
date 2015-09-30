package server;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.*;

/**
 * @author David david.bajko@senacor.com
 */


public class MessageBrowser {

    private final Logger logger = Logger.getLogger(MessageBrowser.class);

    private Session session;
    private ActiveMQConnection activeMQConnection;

    public MessageBrowser(String activeMQUrl) {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQUrl);

        try {

            activeMQConnection = (ActiveMQConnection) connectionFactory.createConnection();
            activeMQConnection.start();
            session = activeMQConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        } catch (JMSException messageCounterException) {
            logger.debug(messageCounterException.getErrorCode() + " " + messageCounterException.getMessage());
            messageCounterException.printStackTrace();
        }

    }


    public List<Integer> getMessageCountOnQueue() {

        List<Integer> listOfCountOfMessagesInQueue = new ArrayList();
        try {
            Set<ActiveMQQueue> queues = activeMQConnection.getDestinationSource().getQueues();
            logger.debug(session);
            Iterator<ActiveMQQueue> queueIterator = queues.iterator();

            String queueName = null;

            while (queueIterator.hasNext()) {

                ActiveMQQueue activeMQQueue = queueIterator.next(); // get next activeMQ queue
//                String queueName = activeMQQueue.getQueueName();// name of current activeMQ queue

                QueueBrowser queueBrowser = session.createBrowser(activeMQQueue);//create queueBrowser
                Enumeration enumeration = queueBrowser.getEnumeration();

                int size = Collections.list(enumeration).size();
                listOfCountOfMessagesInQueue.add(size);
            }

            closeSessison();

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NullPointerException activeMQNullException) {
            System.err.println("ExceptionMessage " + activeMQNullException.getMessage());
            activeMQNullException.printStackTrace();
        }

        return listOfCountOfMessagesInQueue;
    }

    private void closeSessison() throws JMSException {
        session.close();
        activeMQConnection.close();
    }


}
