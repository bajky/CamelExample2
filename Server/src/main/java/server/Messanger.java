package server;

import org.springframework.stereotype.Service;

/**
 * @author David david.bajko@senacor.com
 */

@Service
public class Messanger implements Component {
    public String saySomething(String message) {
        return "Messanger say: " + message;
    }
}
