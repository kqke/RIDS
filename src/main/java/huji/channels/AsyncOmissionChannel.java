package huji.channels;

import huji.channels.constraints.CommunicationConstraints;
import huji.messages.Message;

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

    @Override
    public void send(Message message) {
        if ( !constraints.getConstraint(message.from, message.to) ) {
            final int rand = random.nextInt(10);
            if ( rand < 4 )
                message.setDelay(random.nextInt(1000));
            if ( rand < 6 )
                message.setDelay(random.nextInt(3000));
            if ( rand < 9 )
                message.setDelay(random.nextInt(5000));
            else
                message.setDelay(random.nextInt(10000));

            super.send((T) message);
        }
    }
}
