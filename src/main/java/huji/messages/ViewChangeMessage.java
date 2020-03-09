package huji.messages;

public class ViewChangeMessage extends Message{

    public ViewChangeMessage(int view, int from, int to) {
        super(Type.VC, view, from, to);
    }
}
