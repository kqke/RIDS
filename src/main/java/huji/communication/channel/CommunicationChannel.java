package huji.communication.channel;

public interface CommunicationChannel<T> extends Runnable {
    public int register();
    public boolean send(int from, int to, T msg);
}
