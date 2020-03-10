package huji.logger;

import java.util.Iterator;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class Logger implements Iterable<Log> {
    private Queue<Log> logs;

    public Logger() {
        logs = new ConcurrentLinkedQueue<>();
    }

    public void addLog( Log log ) {
        logs.add(log);
    }

    @Override
    public Iterator<Log> iterator() {
        return logs.iterator();
    }

    @Override
    public void forEach(Consumer action) {
        logs.forEach(action);
    }

    @Override
    public Spliterator<Log> spliterator() {
        return logs.spliterator();
    }
}
