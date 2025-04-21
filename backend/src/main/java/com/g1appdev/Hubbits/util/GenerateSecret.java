package com.g1appdev.Hubbits.util;

import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Utility class to generate a secure Base64-encoded secret key for JWT signing.
 * This uses HMAC-SHA256, a trusted cryptographic algorithm for secure tokens.
 */
public class GenerateSecret {

    public static void main(String[] args) {
        try {
            // Initialize a KeyGenerator for HMAC-SHA256
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256); // Generate a 256-bit key

            // Generate the secret key
            SecretKey secretKey = keyGen.generateKey();

            // Encode the key to Base64 format
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            // Print the Base64-encoded key
            System.out.println("Base64 Encoded Key for JWT Signing:");
            System.out.println(base64Key);
        } catch (Exception e) {
            System.err.println("Error generating the secret key: " + e.getMessage());
        }
    }
}
