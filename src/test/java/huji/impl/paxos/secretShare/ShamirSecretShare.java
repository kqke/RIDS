package huji.impl.paxos.secretShare;

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ShamirSecretShare implements SecretShare {
    final private int K;
    final private BigInteger N;
    final private BigInteger P;
    final private Random random;
    final private Map<Integer, BigInteger[]> polynomials;

    public ShamirSecretShare(final int N, final int F) {
        this.K = N - F;
        this.N = BigInteger.valueOf(N);
        this.P = getPrime(N);
        this.random = new Random();
        this.polynomials = new ConcurrentHashMap<>();
    }

    // Prime

    private static BigInteger getPrime( int n ) {
       do {
            ++n;
       } while ( ! isPrime( n ) );

       return BigInteger.valueOf(n);
    }

    private static boolean isPrime( int n ) {
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

    // Encode

    @Override
    public int encode(int view, int id) {
        if ( ! polynomials.containsKey(view) )
            polynomials.putIfAbsent(view, getCoefficients());

        return compute(
                polynomials.get(view),
                id + 1
        );
    }

    private BigInteger[] getCoefficients() {
        BigInteger[] coefficients = new BigInteger[K];
        coefficients[0] = randomZp(N);
        for (int j = 1; j < K; ++ j ) {
            coefficients[j] = randomZp(P);
        }
        return coefficients;
    }

    private BigInteger randomZp(final BigInteger p) {
        while (true) {
            final BigInteger r = new BigInteger(p.bitLength(), random);
            if ( r.compareTo(BigInteger.ZERO) >= 0 && r.compareTo(p) < 0 ) {
                return r;
            }
        }
    }

    private int compute( BigInteger[] coefficients, int x ) {
        BigInteger accum = coefficients[0];
        for (int j = 1; j < K; j++) {
            final BigInteger t1 = BigInteger.valueOf(x).modPow(BigInteger.valueOf(j), P);
            final BigInteger t2 = coefficients[j].multiply(t1).mod(P);
            accum = accum.add(t2).mod(P);
        }

        return accum.intValue();
    }

    // Decode

    @Override
    public int decode(int view, Map<Integer, Integer> shared_secrets) {
        BigInteger accum = BigInteger.ZERO;

        for ( Map.Entry<Integer, Integer> shared : shared_secrets.entrySet() ) {
            int i = shared.getKey();

            BigInteger enumerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for ( int j : shared_secrets.keySet() ) {
                if ( i != j ) {
                    enumerator = enumerator.multiply(BigInteger.valueOf(-j - 1)).mod(P);
                    denominator = denominator.multiply(BigInteger.valueOf(i - j)).mod(P);
                }
            }

            final BigInteger value = BigInteger.valueOf( shared.getValue() );
            final BigInteger tmp = value.multiply(enumerator).multiply(denominator.modInverse(P)).mod(P);
            accum = accum.add(P).add(tmp).mod(P);
        }

        return accum.intValue();
    }
}
