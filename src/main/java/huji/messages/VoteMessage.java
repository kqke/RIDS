package huji.messages;

public class VoteMessage extends Message {
    public VoteMessage(int view, int from, int to) {
        super("vote", view, from, to);
    }

    @Override
    public String toString() {
        return super.toString() + ", Vote to";
    }
}
