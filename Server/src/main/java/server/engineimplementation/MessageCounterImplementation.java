package server.engineimplementation;

import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Set;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageCounterImplementation {

    private MessageBrowserEngine messageBrowserEngine;
    private Logger logger = Logger.getLogger(MessageCounterImplementation.class);
    private MessageListenerForCount messageListenerForCount;

    public MessageCounterImplementation(String activeMQUrl, String queueName) {
        messageBrowserEngine = new MessageBrowserEngine(activeMQUrl);
        messageListenerForCount = new MessageListenerForCount(activeMQUrl, queueName);
    }

    /**
     * @param
     * @return
     */
    public boolean messageWasDequeued(String queueName, String dlqName, String text) {

        Set<Message> incommingMessages = messageListenerForCount.getIncommingMessages(2000);
        Message resultMessage = messageBrowserEngine.getMessageByText(incommingMessages, text);

        try {
            if (resultMessage != null) {

                String messageID = resultMessage.getJMSMessageID();
                boolean messageOnQueue = messageBrowserEngine.isMessageOnQueue(messageID, queueName);
                boolean messageIsOnDLQ = messageBrowserEngine.isMessageOnQueue(messageID,dlqName);

                if (!messageOnQueue && messageIsOnDLQ) {
                    logger.debug("message with ID: " + resultMessage.getJMSMessageID() + " was dequeued.");
                    return true;
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean messageIsOnDlq(String queueName, String dlqName, String text){

        Set<Message> incommingMessages = messageListenerForCount.getIncommingMessages(2000);
        Message resultMessage = messageBrowserEngine.getMessageByText(incommingMessages, text);

        try {
            if (resultMessage != null) {

                String messageID = resultMessage.getJMSMessageID();
                boolean messageOnQueue = messageBrowserEngine.isMessageOnQueue(messageID, queueName);
                boolean messageOnAnotherQueue = messageBrowserEngine.isMessageOnAnotherQueue(messageID,dlqName);

                if (!messageOnQueue && messageOnAnotherQueue) {
                    logger.debug("message with ID: " + resultMessage.getJMSMessageID() + " was dequeued.");
                    return true;
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Message getDequeuedMessage(String queueName, String dlqName, String text, long timeDelay) {

        Set<Message> incommingMessages = messageListenerForCount.getIncommingMessages(timeDelay);
        Message resultMessage = messageBrowserEngine.getMessageByText(incommingMessages, text);

        try {
            if (resultMessage != null) {

                String messageID = resultMessage.getJMSMessageID();
                boolean messageOnQueue = messageBrowserEngine.isMessageOnQueue(queueName, messageID);
                boolean messageOnAnotherQueue = messageBrowserEngine.isMessageOnAnotherQueue(dlqName);

                if (!messageOnQueue && messageOnAnotherQueue) {
                    logger.debug("message with ID: " + resultMessage.getJMSMessageID() + " was returned.");
                    return resultMessage;
                }
            }

        } catch (JMSException e) {
            logger.debug("Exception in class " + this.getClass().getName());
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnections() {
        this.messageListenerForCount.closeConnection();
        this.messageBrowserEngine.closeConnection();
    }


}
