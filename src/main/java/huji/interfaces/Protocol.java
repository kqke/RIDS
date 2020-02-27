package huji.interfaces;

import huji.logs.Logger;

public interface Protocol extends Runnable {
    void setLogger(Logger logger);
}