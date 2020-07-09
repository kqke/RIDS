package huji.messages;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Delayable implements Delayed {
    private long start_time;

    public Delayable() {
        start_time = System.currentTimeMillis();
    }

    public void setDelay(long delay_in_milliseconds) {
        start_time += delay_in_milliseconds;
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert( start_time - System.currentTimeMillis() , timeUnit );
    }

    @Override
    public int compareTo(Delayed delayed) {
        long comp = getDelay( TimeUnit.MILLISECONDS ) - delayed.getDelay( TimeUnit.MILLISECONDS );
        return comp > 0 ? 1 :
                comp < 0 ? -1 :
                        0;
    }
}