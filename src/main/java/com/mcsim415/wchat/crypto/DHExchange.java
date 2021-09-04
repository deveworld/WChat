package com.mcsim415.wchat.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DHExchange {
    public BigInteger p, g;

    public DHExchange() {
        int bitLength = 512; // 512 bits
        SecureRandom rnd = new SecureRandom();
        p = BigInteger.probablePrime(bitLength, rnd);
        do {
            g = new BigInteger(bitLength, rnd);
        } while (g.compareTo(BigInteger.valueOf(bitLength)) >= 0);
    }
}
