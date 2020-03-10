package huji.channels;

import huji.messages.Message;
import huji.simulator.Simulator;

public interface Channel extends Runnable {
    void setSimulator(Simulator simulator);
    void sendMessage(Message message);

    void shutdown();
}
