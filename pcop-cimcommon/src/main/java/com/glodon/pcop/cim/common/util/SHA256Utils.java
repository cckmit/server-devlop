package com.glodon.pcop.cim.common.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
 
public class SHA256Utils {
 
    public static String sha256_HMAC(String message, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        byte[] keyBytes = secret.getBytes("UTF-8");
        hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256"));
        String sign = Base64.getEncoder().encodeToString(hmacSha256.doFinal(message.getBytes("UTF-8")));
        return sign;
    }
 
}