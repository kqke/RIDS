package huji.interfaces;

import java.util.HashSet;
import java.util.Set;

public class ConditionsTable<T,S> {
    private final PairMap<T,Boolean, Set<S>> conditions_values;

    public ConditionsTable(Iterable<T> T_values ) {
        this.conditions_values = new PairMap<>();
        for ( T T_value: T_values ) {
            conditions_values.put( T_value, false, new HashSet<>());
        }
    }


}
