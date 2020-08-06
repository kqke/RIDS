package huji.interfaces;

import java.util.HashSet;
import java.util.Set;

public class ConditionsTable<K,V,S> {
    private final PairMap<K,V,Set<S>> conditions_values;

    public ConditionsTable( Iterable<K> k_values, V init_value ) {
        this.conditions_values = new PairMap<>();
        for ( K k_value : k_values ) {
            conditions_values.put(k_value, init_value, new HashSet<>());
        }
    }

    public V getValue( K key ) {
        return conditions_values.getLvalue(key);
    }

    public V setValue( K key, V value ) {
        return conditions_values.setLvalue(key, value);
    }

    public ConditionsTable<K,V,S> register( K key, S value ) {
        conditions_values.getRvalue(key).add(value);
        return this;
    }

    public ConditionsTable<K,V,S> register( K key, Iterable<S> values ) {
        for ( S value : values )
            register(key,value);
        return this;
    }

    public ConditionsTable<K,V,S> unregister( K key, S value ) {
        conditions_values.getRvalue(key).remove(value);
        return this;
    }

    public ConditionsTable<K,V,S> unregister( K key, Iterable<S> values ) {
        for ( S value : values )
            unregister(key,value);
        return this;
    }

    public boolean contains( K key, S value ) {
        return conditions_values.getRvalue(key).contains(value);
    }
}
