package huji.interfaces;

import java.util.Map;

public interface Generator {
    int encode(int view, int id);
    int decode(int view, Map<Integer,Integer> shared_secrets);
}
