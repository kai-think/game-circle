package com.kai.common.utils.cipher;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Encoding {
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final Base64.Encoder Encoder = Base64.getEncoder();
    public static final Base64.Decoder Decoder = Base64.getDecoder();

    public static byte[] encode(byte[] decoded) {
        return Encoder.encode(decoded);
    }

    public static byte[] decode(byte[] encoded) {
        return Decoder.decode(encoded);
    }

    public static byte[] encode(String decoded) {
        return Encoder.encode(decoded.getBytes(CHARSET));
    }

    public static byte[] decode(String encoded) {
        return Decoder.decode(encoded);
    }

    public static String encodeToString(byte[] decoded) {
        return Encoder.encodeToString(decoded);
    }

    public static String decodeToString(byte[] encoded) {
        byte[] decoded = Decoder.decode(encoded);
        return new String(decoded, CHARSET);
    }

    public static String encodeToString(String decoded) {
        return Encoder.encodeToString(decoded.getBytes(CHARSET));
    }

    public static String decodeToString(String encoded) {
        byte[] decoded = Decoder.decode(encoded);
        return new String(decoded, CHARSET);
    }
}
