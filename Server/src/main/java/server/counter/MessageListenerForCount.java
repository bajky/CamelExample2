package server.counter;

import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageListenerForCount extends ConnectableComponent{
    private static final int ACKNOWLEDGE_MODE = Session.CLIENT_ACKNOWLEDGE;
    private static final boolean TRANSACTED = false;

    private final Logger logger = Logger.getLogger(MessageListenerForCount.class);
    private final String queueName;

    private ActiveMQMessageConsumer acctiveMQConsumer;

    protected MessageBrowserEngine messageBrowserEngine;

    private List<Message> messageList;

    public MessageListenerForCount(String activeMQurl, String queueName) {
        super(activeMQurl);
        this.messageBrowserEngine = new MessageBrowserEngine(activeMQurl);
        this.queueName = queueName;
        this.messageList = new ArrayList<Message>();
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
            acctiveMQConsumer.setMessageListener(new MessageReceiver());


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    //return all incomming messages in specific time delay
    public Message getLastIncomming(long delayInMillis) {
        long timeMillis = System.currentTimeMillis();

        while ((System.currentTimeMillis() - delayInMillis) < timeMillis) ;


        Collections.sort(messageList, new Comparator<Message>() {
            public int compare(Message message1, Message message2) {
                try {
                    return Long.valueOf(message1.getJMSTimestamp()).compareTo(Long.valueOf(message1.getJMSTimestamp()));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        if(messageList.size() > 0){

            return messageList.get(messageList.size() - 1);
        }
        return null;
    }

    //listener for messageList
    private class MessageReceiver implements MessageListener {

        public void onMessage(Message message) {

            messageList.add(message);

        }
    }

    public MessageBrowserEngine getMessageBrowserEngine() {
        return this.messageBrowserEngine;
    }


}
