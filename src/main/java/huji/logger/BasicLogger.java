package huji.logger;

import huji.listener.BasicListener;

public class BasicLogger extends Logger {
    public BasicLogger() {
        super();
        setListener(
                new BasicListener()
        );
    }
}
