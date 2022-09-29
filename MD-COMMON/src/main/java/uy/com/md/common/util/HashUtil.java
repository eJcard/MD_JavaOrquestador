package uy.com.md.common.util;

import lombok.SneakyThrows;

import java.math.BigInteger;
import java.security.MessageDigest;

public class HashUtil {

    @SneakyThrows
    public static String toSHA1(String value) {
        String sha1 = "";
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(value.getBytes("utf8"));
        sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
        return sha1;
    }
}
