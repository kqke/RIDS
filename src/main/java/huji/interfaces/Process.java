package huji.interfaces;

import java.io.Closeable;
import java.io.IOException;

public abstract class Process extends Thread implements Closeable {
    private boolean is_run;

    protected Process() {
        is_run = true;
    }

    @Override
    public void close() throws IOException {
        shutdown();
    }

    public void shutdown() {
        is_run = false;
        wakeup();
    }

    public void wakeup(){
        try {
            notify();
        } catch (Exception ignored) {}
    }

    @Override
    public void run() {
        while( is_run )
            if ( running_condition() )
                running_process();
            else
                while ( is_run && ! running_condition() )
                    try {
                        wait();
                    } catch (Exception ignored) {}
    }

    abstract protected boolean running_condition();
    abstract protected void running_process();
}
