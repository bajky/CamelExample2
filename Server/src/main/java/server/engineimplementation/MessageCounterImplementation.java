package server.engineimplementation;

import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Set;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageCounterImplementation {

    private MessageBrowser messageBrowser;
    private Logger logger = Logger.getLogger(MessageCounterImplementation.class);
    private QueueMessageListener queueMessageListener;

    public MessageCounterImplementation(String activeMQUrl, String queueName) {
        messageBrowser = new MessageBrowser(activeMQUrl);
        queueMessageListener = new QueueMessageListener(activeMQUrl, queueName);
    }

    /**
     * @param
     * @return
     */
    public boolean messageWasDequeued(String queueName, String dlqName, String text) {

        Set<Message> incommingMessages = queueMessageListener.getIncommingMessages(2000);
        Message resultMessage = messageBrowser.getMessageByText(incommingMessages, text);

        try {
            if (resultMessage != null) {

                String messageID = resultMessage.getJMSMessageID();
                boolean messageOnQueue = messageBrowser.isMessageOnQueue(messageID, queueName);
                boolean messageIsOnDLQ = messageBrowser.isMessageOnQueue(messageID, dlqName);

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

        Set<Message> incommingMessages = queueMessageListener.getIncommingMessages(2000);
        Message resultMessage = messageBrowser.getMessageByText(incommingMessages, text);

        try {
            if (resultMessage != null) {

                String messageID = resultMessage.getJMSMessageID();
                boolean messageOnQueue = messageBrowser.isMessageOnQueue(messageID, queueName);
                boolean messageOnAnotherQueue = messageBrowser.isMessageOnAnotherQueue(messageID,dlqName);

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

        Set<Message> incommingMessages = queueMessageListener.getIncommingMessages(timeDelay);
        Message resultMessage = messageBrowser.getMessageByText(incommingMessages, text);

        try {
            if (resultMessage != null) {

                String messageID = resultMessage.getJMSMessageID();
                boolean messageOnQueue = messageBrowser.isMessageOnQueue(queueName, messageID);
                boolean messageOnAnotherQueue = messageBrowser.isMessageOnAnotherQueue(dlqName);

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
        this.queueMessageListener.closeConnection();
        this.messageBrowser.closeConnection();
    }


}
