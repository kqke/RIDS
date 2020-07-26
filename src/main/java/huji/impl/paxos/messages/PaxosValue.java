package huji.impl.paxos.messages;

public class PaxosValue {
    public final String value;
    public final int view;

    public PaxosValue(String value, int view){
        this.value = value;
        this.view = view;
    }

    @Override
    public String toString() {
        return value;
    }
}
