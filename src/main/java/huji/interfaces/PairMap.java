package huji.interfaces;

import java.util.HashMap;
import java.util.Map;

public class PairMap<L,R> {
    Map<Integer,Pair<L, R>> map = new HashMap<>();

    public Pair<L, R> put(int key, L lvalue, R rvalue) {
        return map.put(key, new Pair<>(lvalue,rvalue));
    }

    public L getLvalue(int key) {
        Pair<L, R> pair;
        return (pair = map.get(key)) != null ? pair.left : null;
    }

    public R getRvalue(int key) {
        Pair<L, R> pair;
        return (pair = map.get(key)) != null ? pair.right : null;
    }

    public L setLvalue(int key, L lvalue) throws IndexOutOfBoundsException {
        if ( ! map.containsKey(key) )
            throw new IndexOutOfBoundsException();
        return put(key, lvalue, map.get(key).right).left;
    }

    public R setRvalue(int key, R rvalue) throws IndexOutOfBoundsException {
        if ( ! map.containsKey(key) )
            throw new IndexOutOfBoundsException();
        return put(key, map.get(key).left, rvalue).right;
    }

    public void remove(int key, L lvalue, R rvalue) {
        map.remove(key);
    }
}
