package com.kai.common.utils.cipher;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class HexEncoding {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static String encodeToString(byte[] decoded) {
        return Hex.encodeHexString(decoded);
    }

    public static String encodeToString(String decoded) {
        return Hex.encodeHexString(decoded.getBytes(CHARSET));
    }

    public static byte[] decode(String encoded) throws DecoderException {
        return Hex.decodeHex(encoded);
    }

    public static String decodeToString(String encoded) throws DecoderException {
        byte[] decoded = Hex.decodeHex(encoded);
        return new String(decoded, CHARSET);
    }
}
