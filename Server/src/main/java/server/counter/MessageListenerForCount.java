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

    private final String activeMQurl;
    private static boolean TRANSACTED = false;

    private Session session;
    private ActiveMQConnection connection;
    private MessageProducer messageProducer;
    private Logger logger = Logger.getLogger(MessageListenerForCount.class);
    private String queueName;
    private MessageListener messageListener;
    private ActiveMQQueue activeMQQueue;
    private String corelationID = null;



    public MessageListenerForCount(String queueName, String activeMQurl) {
        this.activeMQurl = activeMQurl;
        this.queueName = queueName;
        this.messageListener = new MessageCounter();

    }


    public void startCountOn() {


        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQurl);

        try {

            connection = (ActiveMQConnection) connectionFactory.createConnection(); // creating connection
            connection.start(); // starting connection

            session = connection.createSession(TRANSACTED, ackMode); //creating session
            ActiveMQQueue queue = getQueueByname(queueName);

            activeMQQueue = (ActiveMQQueue) session.createQueue("responseQueue");


            ActiveMQMessageConsumer consumer = (ActiveMQMessageConsumer) session.createConsumer(queue);
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            logger.debug("Error in " + this.getClass().getName() + " at starting");
            e.printStackTrace();
        }
    }

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



    public void stopCount() {
        try {
            session.close();
            connection.stop();
        } catch (JMSException e) {
            logger.debug("exception in " + this.getClass().getName());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MessageListenerForCount messageListenerForCount = new MessageListenerForCount("dead","tcp://localhost:61616");
        messageListenerForCount.startCountOn();
        while(true){

        }
    }

   private class MessageCounter implements MessageListener{

       public void onMessage(Message message) {

           try {
               messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
               messageProducer = session.createProducer(null);
               messageProducer.send(activeMQQueue,message);
               corelationID = message.getJMSCorrelationID();

               System.out.println(corelationID);
           } catch (JMSException e) {
               e.printStackTrace();
           }
       }
   }
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

            System.err.println(enumeration.nextElement().toString());
            closeSession();

            return size;

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NullPointerException activeMQNullException) {
            System.err.println("ExceptionMessage " + activeMQNullException.getMessage());
            activeMQNullException.printStackTrace();
        }

        return null;
    }

    private void closeSession(){
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }



}
