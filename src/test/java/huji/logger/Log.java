package huji.logger;

public class Log {
    public final String data;
    public final LogType type;

    public Log(String data, LogType type) {
        this.data = data;
        this.type = type;
    }
    
    @Override
    public String toString() {
        return data;
    }
}
