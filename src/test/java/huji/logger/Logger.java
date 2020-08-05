package huji.logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logger {
    public final Queue<Log> logs;
    public boolean to_print = false;

    public Logger(){
        this.logs = new ConcurrentLinkedQueue<>();
    }

    public void add(Log log){
        logs.add(log);

        if (to_print) {
            synchronized (System.out) {
                if (to_print)
                    System.out.println(log);
            }
        }
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
