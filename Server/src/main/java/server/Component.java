package server;

import org.springframework.stereotype.Service;

/**
 * Created by Bajky on 8.9.2015.
 */
@Service
public interface Component {
    String saySomething(String message);
}
