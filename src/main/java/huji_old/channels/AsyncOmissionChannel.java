package huji_old.channels;

import huji_old.channels.constraints.CommunicationConstraints;
import huji_old.messages.Message;

import java.util.Random;

public class AsyncOmissionChannel<T extends Message> extends CommunicationChannel<T> {

    private CommunicationConstraints constraints;
    private Random random;

    public AsyncOmissionChannel(int n){
        super();
        this.constraints = new CommunicationConstraints(n);
        this.random = new Random();
    }

    public void setConstraints(CommunicationConstraints communicationConstraints) {
        this.constraints = communicationConstraints;
    }

    public CommunicationConstraints getConstraints() {
        return constraints;
    }

    @Override
    public void send(Message message) {
        if ( !constraints.getConstraint(message.from, message.to) ) {
            final int rand = random.nextInt(10);
            if ( rand < 4 )
                message.setDelay(random.nextInt(500));
            if ( rand < 6 )
                message.setDelay(random.nextInt(1000));
            if ( rand < 9 )
                message.setDelay(random.nextInt(1500));
            else
                message.setDelay(random.nextInt(2000));

            super.send((T) message);
        }
    }
}
