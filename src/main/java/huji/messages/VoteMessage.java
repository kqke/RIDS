package huji.messages;

public class VoteMessage extends Message {

    final public String value;

    public VoteMessage(int view, int from, int to, String value) {
        super(Type.VOTE, view, from, to);
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + ", Vote to";
    }
}
