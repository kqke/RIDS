package huji.protocols.replica;

import huji.messages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaxosProtocol extends ReplicaProtocol {
    private int view;
    private Map<Integer,String> decided;

    private List<String> _values;
    private Map<Integer, Integer> _secrets;
    private Map<Integer, int[]> _decision_counters;

    private int _secrets_counter;
    private int _ack_counter;

    private boolean _is_decide;

    public PaxosProtocol() {
        super();
        decided = new HashMap<>();
        _decision_counters = new HashMap<>();

        view = 0;
        viewChange();
    }

    private void viewChange() {
        _values = new ArrayList<>( N() );
        _secrets = new HashMap<>();

        _ack_counter = F() + 1;
        _secrets_counter = F() + 1;

        _is_decide = false;
    }

    @Override
    protected void handle(Message message) {
        switch (message.type) {
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
        if ( viewChangeIfNeeded(message) )
            return;

        _values.add(message.from,message.value);
        outChannel(
                new AckMessage(view, id(), message.from)
        );
    }

    private void ackMessage(AckMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        if (0 == --_ack_counter) {
            outChannel(
                    new ElectMessage(view, id(), -1, getShareSecret(view))
            );
        }
    }

    private void electMessage(ElectMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        _secrets.put( message.from, message.share );
        if ( 0 == --_secrets_counter ) {
            decide();
        }
    }

    private void decide() {
        if ( _is_decide )
            return;
        _is_decide = true;

        int elected = getSecret( view, _secrets );
        outChannel(
                ( _values.get( elected ) != null ) ?
                        new VoteMessage( view, id(), -1, _values.get(elected) ) :
                        new ViewChangeMessage( view, id(), -1 )
        );
    }

    private void voteMessage(VoteMessage message) {
        viewChangeIfNeeded(message);

        int[] counters = getCounters(message.view);
        if ( 0 == --counters[0] ) {
            decided.put(message.view, message.value);

            ++view;
            viewChange();
        }
    }

    private void vcMessage(ViewChangeMessage message) {
        viewChangeIfNeeded(message);

        int[] counters = getCounters(message.view);
        if ( 0 == --counters[1] ) {
            ++view;
            viewChange();
        }
    }

    private int[] getCounters(int view) {
        return _decision_counters.putIfAbsent( view, new int[]{ F() + 1, F() + 1 } );
    }

    private boolean viewChangeIfNeeded(Message message) {
        if ( view < message.view ) {
            view = message.view;
            return true;
        }

        return false;
    }
}
