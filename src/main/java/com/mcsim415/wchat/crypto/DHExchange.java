package com.mcsim415.wchat.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DHExchange {
    public BigInteger p, g, privateK, finalCommonKey;
    private final int bitLength = 512;

    public void makeCommonKeys() {
        SecureRandom rnd = new SecureRandom();
        p = BigInteger.probablePrime(bitLength, rnd);
        g = new BigInteger(p.bitLength()-1, rnd);
    }

    public void makePrivateKey() {
        SecureRandom rnd = new SecureRandom();
        privateK = new BigInteger(bitLength, rnd);
    }

    public BigInteger getPrivateKey() {
        return g.modPow(privateK, p);
    }

    public void setFinalCommonKey(BigInteger opk) {
        finalCommonKey = opk.modPow(privateK, p);
    }

    public BigInteger getFinalCommonKey() {
        return (finalCommonKey != null) ? finalCommonKey : null;
    }

    public void setP(BigInteger _p) {
        p = _p;
    }

    public void setG(BigInteger _g) {
        g= _g;
    }

    public BigInteger getP() {
        return (p != null) ? p : null;
    }

    public BigInteger getG() {
        return (g != null) ? g : null;
    }
}
