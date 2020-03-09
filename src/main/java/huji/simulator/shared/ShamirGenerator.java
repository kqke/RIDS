package huji.simulator.shared;

import huji.interfaces.Generator;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ShamirGenerator implements Generator {
    final private int N;
    final private int F;

    final private int P;

    private Map<Integer, int[]> polynomials;

    public ShamirGenerator(int N, int F) {
        this.N = N;
        this.F = F;

        int P = N;
        while ( ! isPrime(P) ) {
            ++P;
        }
        this.P = P;

        polynomials = new ConcurrentHashMap<>();
    }

    private boolean isPrime( int n ) {
        if ( n == 2 )
            return true;
        else if ( n % 2 == 0 )
            return false;

        for ( int i = 3; i * i <= n; i = i + 2 ) {
            if ( n % i == 0 )
                return false;
        }

        return true;
    }

    @Override
    public int encode(int view, int id) {
        return compute(
                polynomials.putIfAbsent(view, randomize()),
                ++id
        );
    }

    private int[] randomize() {
        Random rand = new Random();
        int[] result = new int[F+1];

        result[0] = rand.nextInt(N) + 1;
        for ( int i = 1; i < F + 1; ++ i ) {
            result[0] = rand.nextInt(P) + 1;
        }
        return result;
    }

    private int compute( int[] poly, int x ) {
        int sum = 0;
        for ( int i = 0; i < poly.length; ++i )
            sum += poly[i] * Math.pow(x, i);

        return sum % P;
    }

    @Override
    public int decode(int view, Map<Integer, Integer> shared_secrets) {
        return 0;
    }
}
