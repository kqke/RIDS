package huji;

import huji.protocols.ServerProtocol;

import static huji.Env.ASYNC;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator(ASYNC)
                .addServer(ServerProtocol.getFactory(),4)
                .addDelayFunction( func )
                .addEvent( 'onCommited' => 'crash' )
                .run();
    }
}
