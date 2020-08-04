package huji.impl.paxos.messages;

public class PaxosValue implements Comparable<PaxosValue> {
    public final String value;

    public PaxosValue(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value + " " + hashCode();
    }

    @Override
    public int compareTo(PaxosValue other) {
        return other.hashCode() - this.hashCode();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if ( ! ( other instanceof PaxosValue ) )
            return false;

        return other.hashCode() == this.hashCode();
    }
}
