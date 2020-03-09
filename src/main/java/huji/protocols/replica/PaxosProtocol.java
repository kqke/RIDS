package huji.protocols.replica;

import huji.messages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaxosProtocol extends ReplicaProtocol {
    private int view;
    private Map<Integer,String> decided;

    public PaxosProtocol() {
        super();
        decided = new HashMap<>();
        _decision_counters = new HashMap<>();
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

    private List<String> _values;
    private void proposeMessage(ProposeMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        _values.add(message.from,message.value);
        outChannel(
                new AckMessage(view, id(), message.from)
        );
    }

    private int _ack_counter;
    private void ackMessage(AckMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        if (0 == --_ack_counter) {
            outChannel(
                    new ElectMessage(view, id(), -1, 0)
            );
        }
    }

    private List<Integer> _secrets;
    private int _secrets_counter;
    private void electMessage(ElectMessage message) {
        if ( viewChangeIfNeeded(message) )
            return;

        _secrets.add( message.from, message.share );
        if ( 0 == --_secrets_counter ) {
            decide();
        }
    }

    private boolean _is_decide;
    private void decide() {
        if ( _is_decide )
            return;
        _is_decide = true;

        int elected = 0;
        outChannel(
                ( _values.get( elected ) != null ) ?
                        new VoteMessage( view, id(), -1, _values.get(elected) ) :
                        new ViewChangeMessage( view, id(), -1 )
        );
    }

    private Map<Integer,int[]> _decision_counters;
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

    private void viewChange() {
        _values = new ArrayList<>( N() );
        _secrets = new ArrayList<>( N() );

        _ack_counter = F() + 1;
        _secrets_counter = F() + 1;

        _is_decide = false;
    }
}
