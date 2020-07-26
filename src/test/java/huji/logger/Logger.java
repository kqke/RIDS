package huji.logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logger {
    public final Queue<Log> logs;

    public Logger(){
        this.logs = new ConcurrentLinkedQueue<>();
    }

    public boolean add(Log log){
        return logs.add(log);
    }

    public Iterator<Log> get() {
        return logs.iterator();
    }

    // Do not use this method if not needed
    public Iterator<Log> get(int from) {
        Iterator<Log> iter = logs.iterator();
        for( int i = 0; i < from; ++i )
            if (iter.hasNext())
                iter.next();

        return logs.iterator();
    }


}
