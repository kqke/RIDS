package huji.protocols.replica;

import huji.messages.Message;
import huji.messages.ViewMessage;

import java.util.*;

public class PaxosProtocol extends ReplicaProtocol {
    private String[] values;
    private Map<Integer, Integer> secrets;
    private Map<Integer, int[]> decision_counters;

    private int secrets_counter;
    private int ack_counter;

    public PaxosProtocol() {
        super();
        decision_counters = new HashMap<>();
    }

    @Override
    protected boolean handle(Message message) {
        if ( super.handle(message) )
            return true;

        ViewMessage view_message = (ViewMessage) message;

        switch (message.messageType) {
            case PROPOSE:
                proposeMessage(view_message);
                break;
            case ACK:
                ackMessage(view_message);
                break;
            case ELECT:
                electMessage(view_message);
                break;
            case VOTE:
                voteMessage(view_message);
                break;
            case VC:
                vcMessage(view_message);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + message.messageType);
        }
    }

    private void proposeMessage(ViewMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        _values[message.from] = message.value;
        outChannel(
                new AckMessage(view, id(), message.from)
        );
    }

    private void ackMessage(ViewMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        if ( 0 == --_ack_counter ) {
            outChannel(
                    new ElectMessage(view, id(), -1, getShareSecret(view))
            );
        }
    }

    private void electMessage(ViewMessage message) {
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
        }
    }

    private void voteMessage(ViewMessage message) {
        viewChangeIfNeeded(message);

        int[] counters = getCounters(message.view);
        if ( 0 == --counters[0] ) {
            decided.put(message.view, message.value);

            if ( message.value.equals( getClientMessage() ) )
                deleteClientMessage();

            ++view;
            viewChange();
        }
    }

    private void vcMessage(ViewMessage message) {
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
    }
}
