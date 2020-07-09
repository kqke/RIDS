package huji.interfaces;

import java.io.Closeable;
import java.io.IOException;

public abstract class Process implements Runnable, Closeable {
    private boolean is_run;
    private Thread thread;

    protected Process() {
        is_run = true;
    }

    public void shutdown() {
        is_run = false;
    }

    public void wakeup(){
        thread.interrupt();
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        while( is_run )
            running_process();
    }

    abstract protected void running_process();

    @Override
    public void close() throws IOException {
        shutdown();
    }
}
