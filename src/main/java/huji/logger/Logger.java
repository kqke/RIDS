package huji.logger;

import huji.interfaces.Listener;
import huji.logger.logs.Log;
import huji.simulator.Simulator;

import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class Logger implements Iterable<Log> {
    private Queue<Log> _logs;
    private Listener _listener = null;

    private Simulator _simulator;

    Logger() {
        _logs = new ConcurrentLinkedQueue<>();
    }

    public void addLog( Log log ) {
        if ( _listener != null )
            _listener.handle(log);
        _logs.add(log);
        System.out.println(log);
    }

    public void setSimulator( Simulator simulator ) {
        _simulator = simulator;
    }

    void setListener ( Listener listener ) {
        _listener = listener;
        _listener.setSimulator( _simulator );
    }

    @Override
    public Iterator<Log> iterator() {
        return _logs.iterator();
    }

    @Override
    public void forEach(Consumer action) {
        _logs.forEach(action);
    }

    @Override
    public Spliterator<Log> spliterator() {
        return _logs.spliterator();
    }
}
