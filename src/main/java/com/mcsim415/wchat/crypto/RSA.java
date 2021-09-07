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
                encryptedMsgs[i] = new BigInteger(encodedMsg).modPow(e, n).toString();
                i++;
            }
        }
        return encryptedMsgs;
    }

    public String decrypt(String encryptedMsg) {
        BigInteger decryptedMsg = new BigInteger(encryptedMsg).modPow(d, n);
        return decodeMsg(decryptedMsg);
    }

    private String decodeMsg(BigInteger encodedIntMsg) {
        String encodedMsg = encodedIntMsg.toString().substring(1);
        StringBuilder msg = new StringBuilder();
        int chunk = 0;
        while (encodedMsg.length() > chunk) {
            msg.append((char) Integer.parseInt(encodedMsg.substring(chunk, chunk + 7))); // RSA.java:70

            // Exception in thread "Thread-2" java.lang.StringIndexOutOfBoundsException: begin 301, end 308, length 307
            // at java.base/java.lang.String.checkBoundsBeginEnd(String.java:3751)
            // at java.base/java.lang.String.substring(String.java:1907)
            // at com.mcsim415.wchat.crypto.RSA.decodeMsg(RSA.java:70)
            // at com.mcsim415.wchat.crypto.RSA.decrypt(RSA.java:62)
            // at com.mcsim415.wchat.thread.chatReceiveThread.run(chatReceiveThread.java:47)
            chunk += 7;
        }
        return msg.toString();
    }

    private String[] encodeMsg(String msg) {
        int length;
        int nLen = n.toString().length()-1;

        length = (7*msg.length());
        if (length > nLen) {
            length = (int) Math.ceil(length / (double) nLen);
        } else {
            length = 1;
        }

        String[] encodedMsg = new String[length];
        int chunk = 0;
        encodedMsg[0] = "1";
        for (int i=0; i<msg.length(); i++) {
            if (encodedMsg[chunk].length()+7 > nLen && !(msg.length()-8 <= i)) {
                chunk++;
                encodedMsg[chunk] = "1"; // RSA.java:93

                // TODO: FIX THESE ERROR ( Need improving encode algorithm, easier faster and efficiently. )
                // Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: Index 10 out of bounds for length 10
                // at com.mcsim415.wchat.crypto.RSA.encodeMsg(RSA.java:93)
                // at com.mcsim415.wchat.crypto.RSA.encrypt(RSA.java:48)
                // at com.mcsim415.wchat.thread.chatSendThread.sendChat(chatSendThread.java:28)
                // at com.mcsim415.wchat.gui.GuiChat.sendChat(GuiChat.java:193)
                // at com.mcsim415.wchat.gui.GuiChat$4.actionPerformed(GuiChat.java:132)
                // Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: Index 12 out of bounds for length 12
                // at com.mcsim415.wchat.crypto.RSA.encodeMsg(RSA.java:93)
                // at com.mcsim415.wchat.crypto.RSA.encrypt(RSA.java:48)
                // at com.mcsim415.wchat.thread.chatSendThread.sendChat(chatSendThread.java:28)
                // at com.mcsim415.wchat.gui.GuiChat.sendChat(GuiChat.java:193)
                // at com.mcsim415.wchat.gui.GuiChat$4.actionPerformed(GuiChat.java:132)
                // Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: Index 12 out of bounds for length 12
                // at com.mcsim415.wchat.crypto.RSA.encodeMsg(RSA.java:93)
                // at com.mcsim415.wchat.crypto.RSA.encrypt(RSA.java:48)
                // at com.mcsim415.wchat.thread.chatSendThread.sendChat(chatSendThread.java:28)
                // at com.mcsim415.wchat.gui.GuiChat.sendChat(GuiChat.java:193)
                // at com.mcsim415.wchat.gui.GuiChat$4.actionPerformed(GuiChat.java:132)
                // Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: Index 10 out of bounds for length 10
                // at com.mcsim415.wchat.crypto.RSA.encodeMsg(RSA.java:93)
                // at com.mcsim415.wchat.crypto.RSA.encrypt(RSA.java:48)
                // at com.mcsim415.wchat.thread.chatSendThread.sendChat(chatSendThread.java:28)
                // at com.mcsim415.wchat.gui.GuiChat.sendChat(GuiChat.java:193)
                // at com.mcsim415.wchat.gui.GuiChat$4.actionPerformed(GuiChat.java:132)
            }
            encodedMsg[chunk] += String.format("%07d", (int) msg.charAt(i));
        }
        return encodedMsg;
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
        int bitLength = 512;
        return BigInteger.probablePrime(bitLength, rnd);
    }
}
