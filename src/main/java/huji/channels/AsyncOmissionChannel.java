package huji.channels;

import huji.channels.constraints.CommunicationConstraints;
import huji.messages.Message;

import java.util.Random;

public class AsyncOmissionChannel<T extends Message> extends CommunicationChannel<T> {
    CommunicationConstraints constraints;
    Random random;

    public AsyncOmissionChannel(int n){
        super();
        this.constraints = new CommunicationConstraints(n);
        this.random = new Random();
    }

    public CommunicationConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(CommunicationConstraints communicationConstraints) {
        this.constraints = communicationConstraints;
    }

    @Override
    public void send(Message message) {
        if(!constraints.getConstraint(message.from, message.to)) {
//            TODO - currently works only with bounded delays
            message.setDelay(random.nextInt(1000));
            super.send((T) message);
        }
    }
}
