package huji.channels;

import huji.channels.constraints.CommunicationConstraints;
import huji.messages.Message;

public class OmissionChannel<T extends Message> extends CommunicationChannel<T> {

    CommunicationConstraints constraints;

    public OmissionChannel(int n){
        super();
        this.constraints = new CommunicationConstraints(n);
    }

    public CommunicationConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(CommunicationConstraints communicationConstraints) {
        this.constraints = communicationConstraints;
    }

    @Override
    public void send(Message message) {
        if(!constraints.getConstraint(message.from, message.to))
            super.send((T) message);
    }
}
