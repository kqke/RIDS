package huji.impl.ViewChangeAble;

import huji.channel.CommunicationChannel;
import huji.impl.ViewChangeAble.messages.ViewAbleMessage;
import huji.impl.ViewChangeAble.messages.ViewAbleType;
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
    protected boolean running_condition() {
        return super.running_condition() || !sent_this_view;
    }

    @Override
    protected boolean handle(Message<T> msg){
        if ( super.handle(msg) )
            return true;

        if ( !(msg instanceof ViewAbleMessage) )
            return false;

        ViewAbleMessage<T> viewable_msg = (ViewAbleMessage<T>)msg;
        view_change_if_needed(viewable_msg);
        if( ! sent_this_view ) {
            send_view_beginning_message();
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

        return to_ignore(viewable_msg);
    }

    protected boolean to_ignore(ViewAbleMessage<T> msg) {
        return (msg.view < this.view) || (msg.storage < this.storage);
    }

    protected void view_change_if_needed(ViewAbleMessage<T> msg) {
        if ( msg.view > this.view() )
            view_update(msg.view);

        if ( msg.storage > this.storage ) {
            req_history(this.storage, msg.storage);
            storage_update(msg.storage);
        }
    }

    private void view_changed() {
        sent_this_view = false;
        on_view_change();
    }

    protected void on_view_change(){}

    /*
     * Storage able
     */
    protected int storage() {
        return storage;
    }

    private void storage_update() {
        ++this.storage;
        view_changed();
        on_storage_update();
    }

    private void storage_update(int storage) {
        this.storage = storage;
        view_changed();
        on_storage_update();
    }

    protected void on_storage_update(){}

    /*
     * View Change able
     */
    protected void view_update(){
        ++this.view;
        view_changed();
        on_view_update( this.view, view );
    }

    protected void view_update(int view){
        this.view = view;
        view_changed();
        on_view_update( this.view, view );
    }

    protected int view() {
        return view;
    }

    protected void on_view_update( int old_view, int view ){}
    protected abstract void send_view_beginning_message();

    /*
     * History
     */

    @Override
    protected Map<Integer, T> get_committed(int start, int end) {
        return super.get_committed(start, end);
    }

    protected void req_history(int start, int end) {
        sendToReplicas(
                new ViewAbleMessage<T>(
                        id,
                        -1,
                        null,
                        view(),
                        storage(),
                        ViewAbleType.HISTORY_REQ
                )
                        .add_property("start", start)
                        .add_property("end", end)
        );
    }

    protected void historyReqMessage(ViewAbleMessage<T> message) {
        Map<Integer,T> history = get_committed(
                message.get_int_property("start"),
                message.get_int_property("end")
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
                        .add_property("history", history)
        );
    }

    protected void historyMessage(ViewAbleMessage<T> message) {
        commit(
                message.get_map_property("history")
        );
    }
}
