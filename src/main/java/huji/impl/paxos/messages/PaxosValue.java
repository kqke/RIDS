package huji.impl.paxos.messages;

public class PaxosValue implements Comparable<PaxosValue> {
    public final String string_value;

    public PaxosValue(String value){
        this.string_value = value;
    }

    @Override
    public String toString() {
        return string_value;
    }

    @Override
    public int compareTo(PaxosValue o) {
        return 0;
    }
}
