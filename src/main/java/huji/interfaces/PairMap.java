package huji.interfaces;

import java.util.HashMap;

public class PairMap<L,R> {
    private final HashMap<Integer, Pair<L, R>> map;

    PairMap() {
        map = new HashMap<>();
    }


}
