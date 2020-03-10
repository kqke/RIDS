package huji.environment;

import huji.events.EventType;
import huji.logger.Log;
import huji.logger.Logger;

public class LogableEnvironment extends Environment {
    private Logger logger;

    public LogableEnvironment() {
        this.logger = null;
    }

    public LogableEnvironment( Logger logger ) {
        this.logger = logger;
    }

    public void setLogger( Logger logger ) {
        this.logger = logger;
    }

    @Override
    public void event(EventType type, String information) {
        if ( logger == null )
            return;

        logger.addLog(
                new Log()
                        .parameter("type",type)
                        .parameter("info",information)
        );
    }
}
