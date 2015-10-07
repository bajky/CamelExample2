package server.counter;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author David david.bajko@senacor.com
 */
public class MessageCounterImplementation extends MessageListenerForCount{

    private MessageBrowserEngine messageBrowserEngine;

    public MessageCounterImplementation(String activeMQUrl, String queueName){
        super(activeMQUrl, queueName);

        messageBrowserEngine = getMessageBrowserEngine();
    }

    public boolean isLastIncMessDeQ(long delay){
        Message lastIncomming = getLastIncomming(delay);
        try {
            return messageBrowserEngine.isMessageOnQueue("dead", lastIncomming.getJMSMessageID());
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return false;
    }

}
