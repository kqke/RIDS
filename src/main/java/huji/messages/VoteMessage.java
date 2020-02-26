package huji.messages;

public class VoteMessage extends Message {

    public VoteMessage(int from, int to) {
        super(from, to);
    }

    @Override
    void msg() {

    }

    @Override
    public String toString() {
        return super.toString() + ", Vote to";
    }
}
