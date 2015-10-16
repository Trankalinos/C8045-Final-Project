package com.example.android.cardemulation;

/**
 * The Security Constants static class is a means to ensure consistency throughout the development
 * of the CardEmulation app, specifically, ensuring that the proper encryption/decryption methods
 * are implemented and utilized.
 */
public class SecurityConstants {
    public static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";

    public static final String TYPE_RSA = "RSA";
    public static final String TYPE_DSA = "DSA";
    public static final String TYPE_BKS = "BKS";
    public static final String TYPE_AES = "AES";
    public static final String TYPE_AES_CFB_NoPadding = "AES/CFB/NoPadding";
    public static final String TYPE_AES_CFB_PKCS5Padding = "AES/CFB/PKCS5Padding";
    public static final String TYPE_AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";

    public static final String SIGNATURE_SHA1withRSA = "SHA1withRSA";
    public static final String SIGNATURE_SHA256withRSA = "SHA256withRSA";
    public static final String SIGNATURE_SHA512withRSA = "SHA512withRSA";
}
