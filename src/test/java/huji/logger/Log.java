package huji.logger;

public class Log {
    private final String data;

    public Log(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data;
    }
}
