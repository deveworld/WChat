package com.mcsim415.wchat.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class RSA {
    private final BigInteger n, e, d;

    public RSA(BigInteger key, String password_org) {
        BigInteger p, q, totient;
        int pi, qi;
        SHA256 sha256 = new SHA256();
        p = makePrime(key + sha256.encrypt(password_org));
        q = makePrime(key + sha256.encrypt(password_org + key));
        pi = 0;
        while (isMersenne(p)) {
            p = makePrime(Integer.toString(pi) + key + sha256.encrypt(password_org));
            pi++;
        }

        qi = 0;
        while (isMersenne(q)) {
            q = makePrime(Integer.toString(qi) + key + sha256.encrypt(password_org + key));
            qi++;
        }

        n = p.multiply(q);
        totient = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));
        if (!totient.mod(BigInteger.valueOf(65537)).equals(BigInteger.valueOf(0))) {
            e = BigInteger.valueOf(65537);
        } else if (!totient.mod(BigInteger.valueOf(3)).equals(BigInteger.valueOf(0))) {
            e = BigInteger.valueOf(3);
        } else {
            e = findE(totient);
        }

        BigInteger[] GcdXY = xgcd(e, totient);

        if (GcdXY[1].compareTo(BigInteger.ZERO) < 0) {
            d = GcdXY[1].add(totient);
        } else {
            d = GcdXY[1];
        }
    }

    public String[] encrypt(String msg) {
        String[] encodedMsgs = encodeMsg(msg);
        String[] encryptedMsgs = new String[encodedMsgs.length];
        int i = 0;
        for (String encodedMsg:encodedMsgs) {
            if (encodedMsg != null) {
                if (i == 0) {
                    encryptedMsgs[i] = encodedMsg;
                } else {
                    encryptedMsgs[i] = new BigInteger(encodedMsg).modPow(e, n).toString();
                }
                i++;
            }
        }
        return encryptedMsgs;
    }

    public String decrypt(String encryptedMsg) {
        return new BigInteger(encryptedMsg).modPow(d, n).toString();
    }

    public String decodeMsg(String encodedMsg) {
        return new String(new BigInteger(encodedMsg).toByteArray());
    }

    private String[] encodeMsg(String msg) {
        int length, chunk = 1;
        int nLen = n.toString().length()-1;

        String encodedMsg = new BigInteger(msg.getBytes()).toString();
        length = encodedMsg.length();
        if (length > nLen) {
            length = (int) Math.ceil(length / (double) nLen);
        } else {
            length = 1;
        }
        length++;

        String[] encodedMsgs = new String[length];
        if (encodedMsg.charAt(0) == '-') {
            encodedMsgs[0] = "-";
            encodedMsg = encodedMsg.substring(1);
        } else {
            encodedMsgs[0] = "+";
        }
        for (int i = 0; i < encodedMsg.length()+1; i += nLen) {
            if (i + nLen > encodedMsg.length()+1) {
                encodedMsgs[chunk] = encodedMsg.substring(i);
            } else {
                encodedMsgs[chunk] = encodedMsg.substring(i, i + nLen);
            }
            chunk++;
        }
        return encodedMsgs;
    }

    private BigInteger[] xgcd(BigInteger a, BigInteger b){
        BigInteger[] retvals = {BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO};
        BigInteger[] aa = {BigInteger.ONE,BigInteger.ZERO};
        BigInteger[] bb = {BigInteger.ZERO,BigInteger.ONE};
        BigInteger q;
        while(true) {
            q = a.divide(b);
            a = a.mod(b);
            aa[0] = aa[0].subtract(q.multiply(aa[1]));
            bb[0] = bb[0].subtract(q.multiply(bb[1]));
            if (a.equals(BigInteger.ZERO)) {
                retvals[0] = b;
                retvals[1] = aa[1];
                retvals[2] = bb[1];
                return retvals;
            }
            q = b.divide(a); b = b.mod(a);
            aa[1] = aa[1].subtract(q.multiply(aa[0]));  bb[1] = bb[1].subtract(q.multiply(bb[0]));
            if (b.equals(BigInteger.ZERO)) {
                retvals[0] = a; retvals[1] = aa[0]; retvals[2] = bb[0];
                return retvals;
            }
        }
    }

    private BigInteger findE(BigInteger totient) {
        SecureRandom rnd = new SecureRandom();
        BigInteger randNum;
        do {
            randNum = new BigInteger(totient.bitLength(), rnd);
        } while (randNum.compareTo(totient) >= 0);
        return randNum;
    }

    private boolean isMersenne(BigInteger n) {
        return (n.add(BigInteger.valueOf(1)).bitCount() == 1);
    }

    private long stringToSeed(String s) {
        if (s == null) {
            return 0;
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L*hash + c;
        }
        return hash;
    }

    private BigInteger makePrime(String seed) {
        Random rnd = new Random();
        rnd.setSeed(stringToSeed(seed));
        int bitLength = 2048;
        return BigInteger.probablePrime(bitLength, rnd);
    }
}
