package server.engineimplementation;

import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashSet;
import java.util.Set;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageListenerForCount extends ConnectableComponent{

    private final Logger logger = Logger.getLogger(MessageListenerForCount.class);
    private final String queueName;

    private ActiveMQMessageConsumer acctiveMQConsumer;

    protected MessageBrowserEngine messageBrowserEngine;

    private Set<Message> messageList;

    public MessageListenerForCount(String activeMQurl, String queueName) {
        super(activeMQurl);
        this.messageBrowserEngine = new MessageBrowserEngine(activeMQurl);
        this.queueName = queueName;
        this.messageList = new HashSet<Message>();
        setMessageListener();
    }

    //close connection and session
    @Override
    protected void closeConnection() {
        try {
            super.closeConnection();
            acctiveMQConsumer.close();
        } catch (JMSException e) {
            logger.debug("exception in " + this.getClass().getName());
            e.printStackTrace();
        }
    }


    //set Listener of the consumer
    private void setMessageListener() {
        try {
            ActiveMQQueue activeMQQueue = messageBrowserEngine.getQueueByname(queueName);

            acctiveMQConsumer = (ActiveMQMessageConsumer) getActiveMQSession().createConsumer(activeMQQueue);
            acctiveMQConsumer.setMessageListener(new IncommingMessagesListener());


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    //return all incomming messages in specific time delay
    public Set<Message> getIncommingMessages(long delayInMillis) {
        long timeMillis = System.currentTimeMillis();

        while ((System.currentTimeMillis() - delayInMillis) < timeMillis) ;

        return messageList;
    }

    //listener for messageList
    private class IncommingMessagesListener implements MessageListener {

        public void onMessage(Message message) {

            messageList.add(message);

        }
    }

    public MessageBrowserEngine getMessageBrowserEngine() {
        return this.messageBrowserEngine;
    }




}
