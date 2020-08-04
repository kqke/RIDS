package huji.impl.dummyUser;

import huji.impl.paxos.messages.PaxosValue;

public class DummyValue implements Comparable<DummyValue> {
    public final String value;

    public DummyValue(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value + " " + hashCode();
    }

    @Override
    public int compareTo(DummyValue other) {
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
