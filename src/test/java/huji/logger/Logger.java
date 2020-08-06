package huji.logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logger {
    public final Queue<Log> logs;
    private final Map<Conditions,Boolean> prints_conditions;
    private final Map<Conditions,LogType> type_register_to_condition;
    public enum Conditions {
        print_all,

    }

    public Logger(){
        this.logs = new ConcurrentLinkedQueue<>();

        this.prints_conditions = new HashMap<>();
        for ( Conditions condition : Conditions.values() ) {
            conditionFalse( condition );
        }

        this.type_register_to_condition = new HashMap<>();

    }

    public void add(Log log){
        logs.add(log);
        for ( Conditions condition : Conditions.values() ) {
            printCondition( log, condition );
        }
    }

    private void printCondition ( Log log, Conditions condition ) {
        if ( prints_conditions.get(condition) & type_register_to_condition. ) {
            synchronized (System.out) {
                if ( prints_conditions.get(condition) )
                    System.out.println(log);
            }
        }
    }

    public void conditionTrue( Conditions condition ) {
        prints_conditions.put( condition, true );
    }

    public void conditionFalse( Conditions condition ) {
        prints_conditions.put( condition, false );
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
