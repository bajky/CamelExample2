package server.engineimplementation;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.log4j.Logger;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author David david.bajko@senacor.com
 */
public abstract class ConnectableComponent {
    private static final int ACKNOWLEDGE_MODE = Session.AUTO_ACKNOWLEDGE;
    private static final boolean TRANSACTIONAL = false;

    private final Logger logger = Logger.getLogger(this.getClass());
    private String activeMQURL;
    private ActiveMQConnection activeMQConnection;
    private ActiveMQSession activeMQSession;

    protected ConnectableComponent(String activeMQUrl){
        this.activeMQURL = activeMQUrl;
        createConnection();
    }

    private void createConnection() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(activeMQURL);
        try {

            activeMQConnection = (ActiveMQConnection) activeMQConnectionFactory.createConnection();
            activeMQConnection.start();

            activeMQSession = (ActiveMQSession) activeMQConnection.createSession(TRANSACTIONAL, ACKNOWLEDGE_MODE);

        } catch (JMSException jmsException) {
            logger.debug("JMS exception in Message browser at creating ActiveMQ connection");
            jmsException.printStackTrace();
        }
    }
    protected void closeConnection() {
        try {
            activeMQSession.close();
            activeMQConnection.close();
        } catch (JMSException e) {
            logger.debug("Error at closing connection in class " + this.getClass().getName());
            e.printStackTrace();
        }
    }

    protected ActiveMQSession getActiveMQSession() {
        return activeMQSession;
    }

    public ActiveMQConnection getActiveMQConnection() {
        return activeMQConnection;
    }
}
