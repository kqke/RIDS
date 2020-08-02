package huji.impl.paxos.messages;

public class PaxosValue {
    public final String string_value;
    public int int_value;
    public final int view;
    public boolean locked;

    public PaxosValue(String value, int view){
        this.string_value = value;
        this.view = view;
        this.locked = false;
    }

    public PaxosValue setIntVal(int val){
        this.int_value = val;
        return this;
    }

    public int getIntVal(){
        return int_value;
    }

    public void lockOn(){
        this.locked = true;
    }

    public void lockOff(){
        locked = false;
    }

    @Override
    public String toString() {
        return string_value;
    }
}
