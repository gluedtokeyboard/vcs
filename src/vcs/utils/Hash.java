package vcs.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utilities for computing hashes.
 */
public final class Hash {
    /**
     * Computes the SHA-1 hash of the given bytes.
     * @param bytes an array of bytes.
     * @return the SHA-1 hash of the given bytes.
     */
    public static String sha1(byte[] bytes) {
        return sha1(Stream.of(bytes));
    }

    /**
     * Computes the SHA-1 hash of the given strings.
     * @param strings a {@link List} of strings.
     * @return the SHA-1 hash of the given strings.
     */
    public static String sha1(List<String> strings) {
        return sha1(strings.stream().map(s -> s.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Computes the SHA-1 hash of the given inputs.
     * @param inputs a {@link Stream} of byte arrays.
     * @return the SHA-1 hash of the given inputs.
     */
    private static String sha1(Stream<byte[]> inputs) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        inputs.forEach(messageDigest::update);
        return messageDigestToHex(messageDigest);
    }

    /**
     * Computes the digest from the given {@link MessageDigest} object and converts it to a hexadecimal string.
     * @param messageDigest a {@link MessageDigest} object.
     * @return the hexadecimal representation of the computed digest.
     */
    private static String messageDigestToHex(MessageDigest messageDigest) {
        byte[] digest = messageDigest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
