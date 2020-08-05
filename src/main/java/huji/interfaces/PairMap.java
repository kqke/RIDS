package huji.interfaces;

import java.util.HashMap;
import java.util.Map;

public class PairMap<L,R> {
    Map<Integer,Pair<L, R>> map = new HashMap<>();

    public void put(int key, L lvalue, R rvalue) {
        map.put(key, new Pair<>(lvalue,rvalue));
    }

    public L getLvalueOrDefault(int key, L lvalue) {
        Pair<L, R> pair;
        return (pair = map.get(key)) != null ? pair.left : lvalue;
    }

    public L getLvalue(int key) {
        return getLvalueOrDefault(key, null);
    }

    public R getRvalueOrDefault(int key, R rvalue) {
        Pair<L, R> pair;
        return (pair = map.get(key)) != null ? pair.right : rvalue;
    }

    public R getRvalue(int key) {
        return getRvalueOrDefault(key, null);
    }

    public void remove(int key, L lvalue, R rvalue) {
        map.remove(key);
    }
}
