package huji.protocols.replica;

import huji.interfaces.Factory;
import huji.interfaces.Protocol;
import huji.protocols.AbstractProtocol;

class ReplicaProtocol extends AbstractProtocol {



    public ReplicaProtocol() {
        super();
    }

    @Override
    public void run() {

    }

    static public Factory<ReplicaProtocol> getFactory() {
        return ReplicaProtocol::new;
    }

}
