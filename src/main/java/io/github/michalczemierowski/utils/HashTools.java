package io.github.michalczemierowski.utils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashTools {
    public static final String HASH_TYPE = "SHA-256";

    public static String getSHA256Hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_TYPE);

            md.update(text.getBytes());
            byte[] digest = md.digest();

            return DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }
}
