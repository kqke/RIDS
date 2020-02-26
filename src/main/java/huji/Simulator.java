package huji;

import huji.messages.Message;
import huji.protocols.Protocol;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

enum Env { ASYNC, PARTIAL, SYNC }

public class Simulator {
    private Env _env;

    private boolean _isRun = true;
    private Queue<Message> _communication_channel;

    private List<Protocol> _servers;
    private List<Thread> _threads;

    private boolean[][] _restrictions;

    public Simulator() {
        _communication_channel = new ConcurrentLinkedQueue<>();

        _servers = new ArrayList<>();
        _threads = new ArrayList<>();
    }

    public void set_env(Env _env) { this._env = _env; }
    public Env get_env() { return _env; }

    public Simulator addServers(Protocol... servers) {
        for (Protocol protocol : servers) {
            _servers.add( protocol );
            _threads.add( new Thread( protocol ) );
        }
        return this;
    }

    public void run() {
        final int nsize = _servers.size();
        _restrictions = new boolean[nsize][nsize];

        for ( Thread thread : _threads ) {
            thread.start();
        }

        while ( _isRun ) {
            if ( ! _communication_channel.isEmpty() )
                sendMSG( _communication_channel.poll() );
        }
    }

    public void sendMSG( Message msg ) {
        if ( Env.ASYNC.equals(_env) || Env.PARTIAL.equals(_env) ) {

        }
        else if ( canCommunicate( msg.get_from(), msg.get_to() ) ) {
            _servers.get(msg.get_to()).sendMsg( msg );
        }
    }

    private boolean canCommunicate( int from, int to ) {
        return ! _restrictions[from][to];
    }
}