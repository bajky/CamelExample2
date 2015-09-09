package server;

import org.springframework.stereotype.Service;

/**
 * Created by Bajky on 8.9.2015.
 */

@Service
public class MessageWrapper implements Component {

    public String saySomething(String message) {
        return "This is mesasge: " + message;
    }
}
