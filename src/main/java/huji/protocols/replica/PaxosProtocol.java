package huji.protocols.replica;

import huji.logger.logs.Log;
import huji.logger.logs.Type;
import huji.messages.*;
import huji.messages.impl.*;

import java.util.*;

public class PaxosProtocol extends ReplicaProtocol {
    private int view;
    private Map<Integer,String> decided;

    private String[] _values;
    private Map<Integer, Integer> _secrets;
    private Map<Integer, int[]> _decision_counters;

    private int _secrets_counter;
    private int _ack_counter;

    public PaxosProtocol() {
        super();
        decided = new HashMap<>();
        _decision_counters = new HashMap<>();

        view = 0;
    }

    @Override
    protected void offer() {
        outChannel(
                new ProposeMessage(view, id(), -1, getClientMessage())
        );
        addLog(
                new Log(Type.NEW_MESSAGE)
                        .parameter("id",id())
                        .parameter("view",view)
                        .parameter("type","propose")
                        .parameter("message", getClientMessage())
        );
    }

    @Override
    protected void handle(Message message) {
        switch (message.type) {
            case CLIENT:
                clientMessage((ClientMessage)message);
                break;
            case PROPOSE:
                proposeMessage((ProposeMessage)message);
                break;
            case ACK:
                ackMessage((AckMessage)message);
                break;
            case ELECT:
                electMessage((ElectMessage)message);
                break;
            case VOTE:
                voteMessage((VoteMessage)message);
                break;
            case VC:
                vcMessage((ViewChangeMessage)message);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.type);
        }
    }

    private void proposeMessage(ProposeMessage message) {
        addLog(
                new Log(Type.GOT_MESSAGE)
                        .parameter("id",id())
                        .parameter("view",view)
                        .parameter("from",message.from)
                        .parameter("type","propose")
                        .parameter("value",message.value)
                        .parameter("msg_view",message.view)
        );
        if ( viewChangeIfNeeded(message) )
            return;

        _values[message.from] = message.value;
        outChannel(
                new AckMessage(view, id(), message.from)
        );
        addLog(
                new Log(Type.NEW_MESSAGE)
                        .parameter("id",id())
                        .parameter("view",view)
                        .parameter("type","ack")
                        .parameter("to",message.from)
        );
    }

    private void ackMessage(AckMessage message) {
        addLog(
                new Log(Type.GOT_MESSAGE)
                        .parameter("id",id())
                        .parameter("view",view)
                        .parameter("from",message.from)
                        .parameter("type","ack")
                        .parameter("msg_view",message.view)
        );
        if ( viewChangeIfNeeded(message) )
            return;

        if ( 0 == --_ack_counter ) {
            outChannel(
                    new ElectMessage(view, id(), -1, getShareSecret(view))
            );
            addLog(
                    new Log(Type.NEW_MESSAGE)
                            .parameter("id",id())
                            .parameter("view",view)
                            .parameter("type","elect")
            );
        }
    }

    private void electMessage(ElectMessage message) {
        addLog(
                new Log(Type.GOT_MESSAGE)
                        .parameter("id",id())
                        .parameter("view",view)
                        .parameter("from",message.from)
                        .parameter("type","elect")
                        .parameter("share",message.share)
                        .parameter("msg_view",message.view)
        );
        if ( viewChangeIfNeeded(message) )
            return;

        _secrets.put( message.from, message.share );
        if ( 0 == --_secrets_counter ) {
            int elected = getSecret( view, _secrets );
            outChannel(
                    ( _values[ elected ] != null ) ?
                            new VoteMessage( view, id(), -1, _values[ elected ] ) :
                            new ViewChangeMessage( view, id(), -1 )
            );
            addLog(
                    new Log(Type.NEW_MESSAGE)
                            .parameter("id",id())
                            .parameter("view",view)
                            .parameter("type", ( _values[ elected ] != null ) ? "vote" : "vc")
            );
        }
    }

    private void voteMessage(VoteMessage message) {
        addLog(
                new Log(Type.GOT_MESSAGE)
                        .parameter("id",id())
                        .parameter("view",view)
                        .parameter("from",message.from)
                        .parameter("type","vote")
                        .parameter("value",message.value)
                        .parameter("msg_view",message.view)
        );
        viewChangeIfNeeded(message);

        int[] counters = getCounters(message.view);
        if ( 0 == --counters[0] ) {
            decided.put(message.view, message.value);

            if ( message.value.equals( getClientMessage() ) )
                deleteClientMessage();

            addLog(
                    new Log(Type.DECIDE)
                            .parameter("id",id())
                            .parameter("view",view)
                            .parameter("value",message.value)
            );

            ++view;
            viewChange();
        }
    }

    private void vcMessage(ViewChangeMessage message) {
        addLog(
                new Log(Type.GOT_MESSAGE)
                        .parameter("id",id())
                        .parameter("view",view)
                        .parameter("from",message.from)
                        .parameter("type","vc")
                        .parameter("msg_view",message.view)
        );
        viewChangeIfNeeded(message);

        int[] counters = getCounters(message.view);
        if ( 0 == --counters[1] ) {
            ++view;
            viewChange();
        }
    }

    // Helpers

    private int[] getCounters(int view) {
        _decision_counters.putIfAbsent( view, new int[]{ F() + 1, F() + 1 } );
        return _decision_counters.get(view);
    }

    // View Change

    private boolean viewChangeIfNeeded(Message message) {
        if ( view < message.view ) {
            view = message.view;
            viewChange();
            return true;
        }

        return false;
    }

    @Override
    void viewChange() {
        _values = new String[N()];
        _secrets = new HashMap<>();

        _ack_counter = F() + 1;
        _secrets_counter = F() + 1;

        addLog(
                new Log(Type.VIEW_CHANGE)
                        .parameter("id",id())
                        .parameter("view",view)
        );
    }
}
