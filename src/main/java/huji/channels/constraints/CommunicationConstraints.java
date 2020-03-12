package huji.channels.constraints;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;

public class CommunicationConstraints {

    Set<Pair<Integer, Integer>> constraints;

    int nReplicas;

    public CommunicationConstraints(int n){
        nReplicas = n;
        constraints = new HashSet<>();
    }

    // TODO - currently only adds omission on the communication between replicas;

    public void setOmission(int id){
        for(int i = 0 ; i < nReplicas; i++){
            setTwoWayOmission(i, id);
        }
    }

    public void removeOmission(int id){
        for(int i = 0 ; i < nReplicas; i++){
            removeTwoWayOmission(i, id);
        }
    }

    public void setTwoWayOmission(int from, int to){
        setOmissionChannel(from, to);
        setOmissionChannel(to, from);
    }

    public void removeTwoWayOmission(int from, int to){
        removeOmissionChannel(from, to);
        removeOmissionChannel(to, from);
    }

    public void setOmissionChannel(int from , int to){
        constraints.add(new Pair<>(from, to));
    }

    public void removeOmissionChannel(int from, int to){
        constraints.remove(new Pair<>(from, to));
    }

    public boolean getConstraint(int from, int to){
        return constraints.contains(new Pair<>(from, to));
    }
}
