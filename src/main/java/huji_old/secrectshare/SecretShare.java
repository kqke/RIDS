package huji_old.secrectshare;

import java.util.Map;

public interface SecretShare {
    int encode(int view, int id);
    int decode(int view, Map<Integer,Integer> shared_secrets);
}
