package huji.interfaces;

import huji.logs.Logger;

public interface Protocol extends Runnable {
    void setLogger(Logger logger);
    void addLog(Log log);

    void setChannel(Channel communication);
}