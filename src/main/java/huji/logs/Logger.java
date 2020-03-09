package huji.logs;

import huji.interfaces.Listener;
import huji.interfaces.Log;

import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class Logger implements Iterable<Log> {
    private Queue<Log> _logs;
    private Listener _listener = null;

    public Logger() {
        _logs = new ConcurrentLinkedQueue<>();
    }

    public void addLog( Log log ) {
        if ( _listener != null )
            _listener.handle(log);
        _logs.add(log);
    }

    public void setListener ( Listener listener ) {
        _listener = listener;
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
