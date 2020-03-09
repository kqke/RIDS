package huji.messages;

public class ProposeMessage extends Message{

    final public String value;

    public ProposeMessage(int view, int from , int to, String value) {
        super(Type.PROPOSE, view, from, to);
        this.value = value;
    }


}
