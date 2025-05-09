package com.imdealone.open.demo.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

public final class ImdealSignatureUtils {

    private ImdealSignatureUtils() {
    }

    private static final String SIGNATURE_ALGORITHM = "HmacSHA256";

    public static String sha256(String body) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(body.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    public static byte[] hmacSha256(String secret, String canonicalRequest) throws NoSuchAlgorithmException, InvalidKeyException {
        return hmacSha256(secret.getBytes(StandardCharsets.UTF_8), canonicalRequest.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] hmacSha256(byte[] secret, byte[] canonicalRequest) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
        mac.init(new SecretKeySpec(secret, SIGNATURE_ALGORITHM));
        return mac.doFinal(canonicalRequest);
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        //return String.format("%064x", new java.math.BigInteger(1, bytes));
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }

    public static String encode(String value, Charset charset) {
        try {
            return URLEncoder.encode(value, charset.name());
        } catch (UnsupportedEncodingException ignored) {
            return null;
        }
    }

    /**
     * 创建规范请求
     *
     * @param method HTTP方法(GET, PUT, POST, etc.)
     */
    public static String canonicalRequest(String method, String path, TreeMap<String, String> queryParams, TreeMap<String, String> headers, String body) throws NoSuchAlgorithmException {
        StringBuilder canonicalRequest = new StringBuilder();

        /* Step 1.1 以HTTP方法(GET, PUT, POST, etc.)开头, 然后换行. */
        canonicalRequest.append(method).append("\n");
        /* Step 1.2 添加Path参数，换行. */
        path = path == null || path.trim().isEmpty() ? "/" : path;
        canonicalRequest.append(path).append("\n");

        /* Step 1.3 添加查询参数，换行. */
        StringBuilder queryString = new StringBuilder();
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entrySet : queryParams.entrySet()) {
                String key = encode(entrySet.getKey(), StandardCharsets.UTF_8);
                String value = encode(entrySet.getValue(), StandardCharsets.UTF_8);
                queryString.append(key).append("=").append(value).append("&");
            }
            queryString.deleteCharAt(queryString.lastIndexOf("&"));
            queryString.append("\n");
        } else {
            queryString.append("\n");
        }
        canonicalRequest.append(queryString);
        /* Step 1.4 添加headers, 每个header都需要换行. */
        for (Map.Entry<String, String> entrySet : headers.entrySet()) {
            String key = entrySet.getKey().toLowerCase();
            String value = entrySet.getValue().trim();
            canonicalRequest.append(key).append(":").append(value).append("\n");
        }
        canonicalRequest.append("\n");

        String hashedBody = sha256(body);

        canonicalRequest.append(hashedBody);

        return canonicalRequest.toString();
    }


}
