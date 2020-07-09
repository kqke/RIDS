package huji.interfaces;

import java.io.Closeable;
import java.io.IOException;

public abstract class Process implements Runnable, Closeable {
    private boolean is_run;

    protected Process() {
        is_run = true;
    }

    public void shutdown() {
        is_run = false;
    }

    @Override
    public void run() {
        while( is_run )
            running_process();
    }

    abstract protected void running_process();

    @Override
    public void close() throws IOException {
        shutdown();
    }
}
