package server.test;

import server.counter.MessageListenerForCount;

/**
 * Created by Bajky on 6.10.2015.
 */
public class Main {
    public static void main(String[] args) {
        MessageListenerForCount messageListenerForCount = new MessageListenerForCount("tcp://localhost:61616", "dead");
        boolean messageOnQueue = messageListenerForCount.isMessageOnQueue("dead", "asd");

        System.err.println(messageOnQueue);


    }
}
