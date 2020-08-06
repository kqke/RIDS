package huji.logger;

import huji.interfaces.ConditionsTable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logger {
    public final Queue<Log> logs;
    private final ConditionsTable<Conditions,Boolean,LogType> conditions_tbl;
    public enum Conditions {
        print_all,
        print_protocol_events,
        print_commits
    }

    public Logger(){
        this.logs = new ConcurrentLinkedQueue<>();

        this.conditions_tbl = new ConditionsTable<>(Arrays.asList(Conditions.values()),false);
        this.conditions_tbl.register(Conditions.print_all,Arrays.asList(LogType.values()));
        this.conditions_tbl.register(Conditions.print_protocol_events, Arrays.asList(LogType.values()))
                            .unregister(Conditions.print_protocol_events,LogType.Message);
        this.conditions_tbl.register(Conditions.print_commits,LogType.Commit);
    }

    public void add(Log log){
        logs.add(log);
        for ( Conditions condition : Conditions.values() ) {
            printCondition( log, condition );
        }
    }

    private void printCondition ( Log log, Conditions condition ) {
        if ( conditions_tbl.getValue(condition) & conditions_tbl.contains(condition,log.type)) {
            synchronized (System.out) {
                if ( conditions_tbl.getValue(condition) )
                    System.out.println(log);
            }
        }
    }

    public void conditionTrue( Conditions condition ) {
        conditions_tbl.setValue(condition,true);
    }

    public void conditionFalse( Conditions condition ) {
        conditions_tbl.setValue(condition,false);
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
