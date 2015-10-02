package server.counter;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Random;

/**
 * @author David david.bajko@senacor.com
 */
public class Client implements MessageListener{
    private static int ackMode;
    private static String clientQueueName;

    private boolean transacted = false;
    private MessageProducer producer;

    static {
        clientQueueName = "client.messages";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }

    public Client() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(transacted, ackMode);
            Destination adminQueue = session.createQueue(clientQueueName);

            //Setup a message producer to send message to the queue the server is consuming from
            this.producer = session.createProducer(adminQueue);
            this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);


            Destination tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);


            responseConsumer.setMessageListener(this);

            //Now create the actual message you want to send
            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText("MyProtocolMessage");


            txtMessage.setJMSReplyTo(tempDest);


            String correlationId = this.createRandomString();
            txtMessage.setJMSCorrelationID(correlationId);
            this.producer.send(txtMessage);
        } catch (JMSException e) {
            //Handle the exception appropriately
        }
    }

    private String createRandomString() {
        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }

    public void onMessage(Message message) {
        String messageText = null;
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                messageText = textMessage.getText();
                System.out.println("messageText = " + messageText);
                System.exit(0);
            }
        } catch (JMSException e) {
            //Handle the exception appropriately
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}
