package huji.interfaces;

import java.util.HashMap;
import java.util.Map;

public class PairMap<K,L,R> {
    Map<K,Pair<L, R>> map = new HashMap<>();

    public Pair<L,R> put(K key, L lvalue, R rvalue) {
        return map.put(key, new Pair<>(lvalue,rvalue));
    }

    public L getLvalueOrDefault(K key, L lvalue) {
        Pair<L, R> pair;
        return (pair = map.get(key)) != null ? pair.left : lvalue;
    }

    public L getLvalue(K key) {
        return getLvalueOrDefault(key, null);
    }

    public R getRvalueOrDefault(K key, R rvalue) {
        Pair<L, R> pair;
        return (pair = map.get(key)) != null ? pair.right : rvalue;
    }

    public R getRvalue(K key) {
        return getRvalueOrDefault(key, null);
    }

    public L setLvalue( K key, L lvalue ) {
        if ( !map.containsKey(key) )
            return null;
        return put(key, lvalue, map.get(key).right).left;
    }

    public R setRvalue( K key, R rvalue ) {
        if ( !map.containsKey(key) )
            return null;
        return put(key, map.get(key).left, rvalue).right;
    }


    public void remove(K key, L lvalue, R rvalue) {
        map.remove(key);
    }
}
