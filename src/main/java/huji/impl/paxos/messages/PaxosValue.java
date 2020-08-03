package huji.impl.paxos.messages;

public class PaxosValue {
    public final String string_value;
    public int int_value;
    public final int view;

    public PaxosValue(String value, int view){
        this.string_value = value;
        this.view = view;
    }

    public PaxosValue setIntVal(int val){
        this.int_value = val;
        return this;
    }

    public int getIntVal(){
        return int_value;
    }

    @Override
    public String toString() {
        return string_value;
    }
}
