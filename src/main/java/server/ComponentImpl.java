package server;

import org.springframework.stereotype.Service;

/**
 * Created by Bajky on 8.9.2015.
 */

@Service(value = "componentImpl")
public class ComponentImpl implements Component {

    public String saySomething(String message) {
        return message;
    }
}
