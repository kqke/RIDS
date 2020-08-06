package huji.logger;

public class Log {
    public final LogType type;

    public final int node;
    public final int view;
    public final int storage;

    public final String data;

    public Log(LogType type, int node, int view, int storage, String data) {
        this.type = type;

        this.node = node;
        this.view = view;
        this.storage = storage;

        this.data = data;
    }
    
    @Override
    public String toString() {
        return ("type: " + type.toString())
                + ", "
                + ("node: " + node)
                + ", "
                + ("view: " + view)
                + ", "
                + ("storage: " + storage)
                + ", "
                + ("data: " + data)
                ;
    }
}
