package huji.interfaces;

import java.util.HashMap;
import java.util.Map;

public class PairMap<I,L,R> {
    Map<I,Pair<L, R>> map = new HashMap<>();

    public void put(I key, L lvalue, R rvalue) {
        map.put(key, new Pair<>(lvalue,rvalue));
    }

    public L getLvalueOrDefault(I key, L lvalue) {
        Pair<L, R> pair;
        return (pair = map.get(key)) != null ? pair.left : lvalue;
    }

    public L getLvalue(I key) {
        return getLvalueOrDefault(key, null);
    }

    public R getRvalueOrDefault(I key, R rvalue) {
        Pair<L, R> pair;
        return (pair = map.get(key)) != null ? pair.right : rvalue;
    }

    public R getRvalue(I key) {
        return getRvalueOrDefault(key, null);
    }

    public void remove(I key, L lvalue, R rvalue) {
        map.remove(key);
    }
}
