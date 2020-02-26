package huji.protocols;

import huji.Simulator;
import huji.messages.Message;

import java.util.Queue;

public abstract class Protocol implements Runnable {
    private boolean _isRun = true;
    private Queue<Message> _communication_channel;

    final private Simulator _sim;

    public void run() {
        while ( _isRun ) {
            if ( ! _communication_channel.isEmpty() )
                _communication_channel.poll().run();
        }
    }

    public void sendMsg( Message msg ) {
        _sim.sendMSG( msg );
    }
}
