package huji.impl.viewChangeAble;

import huji.channel.CommunicationChannel;
import huji.impl.viewChangeAble.messages.ViewAbleMessage;
import huji.impl.viewChangeAble.messages.ViewAbleType;
import huji.message.Message;
import huji.node.ReplicaNode;

import java.util.Map;

public abstract class ViewChangeAbleNode<T extends Comparable<T>> extends ReplicaNode<T>  {
    private int storage = 0;
    private int view = 0;
    private boolean sent_this_view = false;

    public ViewChangeAbleNode(CommunicationChannel<T> channel) {
        super(channel);
    }

    @Override
    protected boolean runningCondition() {
        return super.runningCondition() || !sent_this_view;
    }

    @Override
    protected boolean handle(Message<T> msg){
        if ( super.handle(msg) )
            return true;

        if ( !(msg instanceof ViewAbleMessage) )
            return false;

        ViewAbleMessage<T> viewable_msg = (ViewAbleMessage<T>)msg;
        viewChangeIfNeeded(viewable_msg);
        if( ! sent_this_view ) {
            sendViewBeginningMessage();
            sent_this_view = true;
        }

        switch (viewable_msg.type) {
            case HISTORY_REQ:
                historyReqMessage(viewable_msg);
                return true;
            case HISTORY:
                historyMessage(viewable_msg);
                return true;
        }

        return toIgnore(viewable_msg);
    }

    protected boolean toIgnore(ViewAbleMessage<T> msg) {
        return (msg.view < this.view) || (msg.storage < this.storage);
    }

    protected void viewChangeIfNeeded(ViewAbleMessage<T> msg) {
        if ( msg.view > this.view() )
            viewUpdate(msg.view);

        if ( msg.storage > this.storage ) {
            reqHistory(this.storage, msg.storage);
            storageUpdate(msg.storage);
        }
    }

    private void resetSentThisView() {
        sent_this_view = false;
    }

    /*
     * Storage able
     */
    protected int storage() {
        return storage;
    }

    protected void storageUpdate() {
        ++this.storage;
        resetSentThisView();
        onStorageUpdate();
    }

    protected void storageUpdate(int storage) {
        this.storage = storage;
        resetSentThisView();
        onStorageUpdate();
    }

    protected void commit(T value) {
        super.commit(storage(), value);
    }

    protected void onStorageUpdate(){}

    /*
     * View Change able
     */
    protected void viewUpdate(){
        ++this.view;
        resetSentThisView();
        onViewUpdate( this.view, view );
    }

    protected void viewUpdate(int view){
        this.view = view;
        resetSentThisView();
        onViewUpdate( this.view, view );
    }

    protected int view() {
        return view;
    }

    protected void onViewUpdate(int old_view, int view ){}
    protected abstract void sendViewBeginningMessage();

    /*
     * History
     */

    protected void reqHistory(int start, int end) {
        sendToReplicas(
                new ViewAbleMessage<T>(
                        id,
                        -1,
                        null,
                        view(),
                        storage(),
                        ViewAbleType.HISTORY_REQ
                )
                        .addProperty("start", start)
                        .addProperty("end", end)
        );
    }

    protected void historyReqMessage(ViewAbleMessage<T> message) {
        Map<Integer,T> history = getCommitted(
                message.getIntProperty("start"),
                message.getIntProperty("end")
        );

        send(
                new ViewAbleMessage<T>(
                        id,
                        message.from,
                        null,
                        view(),
                        storage(),
                        ViewAbleType.HISTORY
                )
                        .addProperty("history", history)
        );
    }

    protected void historyMessage(ViewAbleMessage<T> message) {
        commit(
                message.getMapProperty("history")
        );
    }
}
