package server.engineimplementation;

import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQQueueReceiver;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author David david.bajko@senacor.com
 */
public class QueueMessageListener extends ConnectableComponent {


    private final Logger logger = Logger.getLogger(QueueMessageListener.class);
    private final String queueName;

    private ActiveMQMessageConsumer acctiveMQConsumer;
    private String activeMQUrl;

    private Set<Message> messageList;

    public QueueMessageListener(String activeMQurl, String queueName) {
        super(activeMQurl);
        this.activeMQUrl = activeMQurl;
        this.queueName = queueName ;
        this.messageList = new HashSet<Message>();
        setMessageConsumer();
    }

    //close connection and session
    @Override
    public void closeConnection() {
        try {
            super.closeConnection();
            acctiveMQConsumer.close();
        } catch (JMSException e) {
            logger.debug("exception in " + this.getClass().getName());
            e.printStackTrace();
        }
    }


    //set Listener of the consumer
    private void setMessageConsumer() {
        try {
            getActiveMQConnection().createSession(false, Session.CLIENT_ACKNOWLEDGE);
            ActiveMQQueue activeMQQueue = new MessageBrowser(activeMQUrl).getQueueByname(queueName);
            acctiveMQConsumer = (ActiveMQMessageConsumer) getActiveMQSession().createConsumer(activeMQQueue, "JMSType = 'error'");

            acctiveMQConsumer.setMessageListener(new IncommingMessagesListener());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    //return all incomming messages in specific time delay
    public Set<Message> getIncommingMessages(long delayInMillis) {
        long timeMillis = System.currentTimeMillis();

        while ((System.currentTimeMillis() - delayInMillis) < timeMillis && messageList.size()==0) ;

        return messageList;
    }


    //listener for messageList
    private class IncommingMessagesListener implements MessageListener {

        public void onMessage(Message message) {

            messageList.add(message);
            System.err.println(message);

        }
    }




}
