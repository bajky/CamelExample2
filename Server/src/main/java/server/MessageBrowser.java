package server;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
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

    private ActiveMQQueue getQueueByname(String name) {

        ActiveMQQueue activeMQQueue = null;
        try {
            Set<ActiveMQQueue> queues = activeMQConnection.getDestinationSource().getQueues();
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


    public Integer getMessageCountOnQueue(String name) {

        try {
            Set<ActiveMQQueue> queues = activeMQConnection.getDestinationSource().getQueues();

            logger.debug(session);
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
            System.out.println(activeMQConnection.getBrokerInfo());

            ActiveMQQueueBrowser queueBrowser = (ActiveMQQueueBrowser) session.createBrowser(activeMQQueue);//create queueBrowser

            Enumeration enumeration = queueBrowser.getEnumeration();
            int size = Collections.list(enumeration).size();
            closeSessison();

            return size;

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NullPointerException activeMQNullException) {
            System.err.println("ExceptionMessage " + activeMQNullException.getMessage());
            activeMQNullException.printStackTrace();
        }

        return null;
    }

    private void closeSessison() throws JMSException {
        session.close();
        activeMQConnection.close();
    }

    public void getCountOfDequeuedMsg(String name) {

        try {
            Queue replyTo = session.createTemporaryQueue();
            MessageConsumer consumer = session.createConsumer(replyTo);

//            Queue queue = getQueueByname("test.queue");
            MessageProducer producer = session.createProducer(null);

            String queueName = "ActiveMQ.Statistics.Destination." + name;
            Queue query = session.createQueue(queueName);

            Message message = session.createMessage();


            message.setJMSReplyTo(replyTo);
            producer.send(query, message);

            MapMessage reply = (MapMessage) consumer.receive();

            if (reply != null && reply.getMapNames().hasMoreElements()) {

                for (Enumeration e = reply.getMapNames(); e.hasMoreElements(); ) {
                    String stringName = e.nextElement().toString();
                    System.err.println(stringName + "=" + reply.getObject(stringName));
                }

            }else{
                logger.debug("Null in getCountOfDequeuedMsg");
            }


        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


}
