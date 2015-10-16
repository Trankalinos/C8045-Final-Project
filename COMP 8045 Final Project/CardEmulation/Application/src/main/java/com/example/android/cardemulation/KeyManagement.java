package com.example.android.cardemulation;

import android.content.Context;
import android.util.Base64;
import com.example.android.common.logger.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * public class KeyManagement
 *
 * This class handles all of the encryption, decryption, signature creation and verification for the
 * app. It also retrieves data from the Account object in order to obtain the proper components for
 * encryption and decryption.
 *
 *      Classes:
 *              public KeyManagement(Context c, String keyAlias, Account account)
 *
 *      Methods:
 *              public boolean createKeys()
 *              private void saveKeys(KeyPair kp)
 *              public String encrypt(String data, PublicKey pubKey)
 *              public String decrypt(String data, PrivateKey privKey)
 *              public String signData(String data)
 *              public boolean verifyData(String input, String signature)
 *              public KeyPair setKeyPair()
 *              public boolean areKeysLoaded()
 *              public String encrypt(byte[] clear)
 *              public String decrypt(byte[] encrypted)
 *              public SecretKey generateAESKey(int bits)
 *
 *      Author: David Tran
 *      Created on: March 9, 2015
 *
 */
public class KeyManagement {

    public static final String TAG = "KeyManagement";

    private boolean keys_loaded = false;
    private KeyPair kp = null;
    private PublicKey publicKey = null;
    private PrivateKey privateKey = null;

    private Context context;
    private Account mAccount;

    private byte[] KEY = "This is a key123".getBytes();
    private byte[] IV = "This is an IV456".getBytes();

    /**
     * Creates a public and private key and stores it using the Android Key Store, so that only
     * this application will be able to access the keys.
     */
    public KeyManagement(Context c, String keyAlias, Account account) throws NoSuchAlgorithmException {
        context = c;
        mAccount = account;
        KEY = mAccount.getSecretKey().getBytes();
        IV = mAccount.getIv().getBytes();
        Log.i(TAG, "IV: " + mAccount.getIv());
    }

    /**
     * public boolean createKeys
     *
     * Creates a key pair and stores it as a KeyPair object
     *
     *
     * @return
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     */
    public boolean createKeys() throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyPairGenerator kpGenerator = KeyPairGenerator
                .getInstance(SecurityConstants.TYPE_RSA);
        kpGenerator.initialize(1024);

        kp = kpGenerator.generateKeyPair();
        Log.d(TAG, "Public Key is: " + kp.getPublic().getEncoded());
        Log.d(TAG, "Private Key is: " + kp.getPrivate().getEncoded());

        saveKeys(kp);
        keys_loaded = true;
        return keys_loaded;
        // END_INCLUDE(create_keypair)
    }

    private void saveKeys(KeyPair kp) {
        try {
            // BufferedWriter buf = new BufferedWriter(new FileWriter(keyFile, true));
            File pubKey = new File(context.getExternalFilesDir(null), mAccount.getPublicKey());
            File privKey = new File(context.getExternalFilesDir(null), mAccount.getPrivateKey());

            FileOutputStream fos = new FileOutputStream(pubKey);
            X509EncodedKeySpec x509 = new X509EncodedKeySpec(kp.getPublic().getEncoded());
            fos.write(x509.getEncoded());
            fos.close();
            Log.i(TAG, "Public Key saved as: " + mAccount.getPublicKey());

            fos = new FileOutputStream(privKey);
            PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(kp.getPrivate().getEncoded());
            fos.write(pkcs8.getEncoded());
            fos.close();
            Log.i(TAG, "Private Key saved as: " + mAccount.getPublicKey());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "ERROR: Keys were not successfully saved...");
        }
    }

    /**
     * public String encrypt
     *
     * This method encrypts the contents using RSA public key. It was used as an experimental test
     * to see if we could encrypt the contents for the NFC reader. It was insufficient and the app
     * discontinued its use for this iteration of the project.
     *
     * @param data
     * @param pubKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public String encrypt(String data, PublicKey pubKey) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException {

        Cipher cipher = Cipher.getInstance(SecurityConstants.TYPE_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        String encrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        return encrypted;
    }

    /**
     * public String decrypt
     *
     * This method decrypts the contents using RSA private key. It was used as an experimental test
     * to see if we could encrypt the contents for the NFC reader. It was insufficient and the app
     * discontinued its use for this iteration of the project. In the future, this method would be
     * utilized to decrypt the incoming secret key from the Server for AES-128 encryption as part of
     * secure key transfer.
     *
     * @param data
     * @param privKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws IOException
     */
    public String decrypt(String data, PrivateKey privKey) throws
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException {

        Cipher cipher = Cipher.getInstance(SecurityConstants.TYPE_RSA);
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] decodedBytes = Base64.decode(data, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        String decrypted = new String(decryptedBytes);
        return decrypted;
    }

    /**
     * public String signData
     *
     * This method takes the String data and generates a digital signature based on this app's
     * private key. It also encodes the signature into Base64 encoding and returns it as a String.
     *
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public String signData(String input) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        byte[] data = input.getBytes();

        Signature s = Signature.getInstance(SecurityConstants.SIGNATURE_SHA256withRSA);

        s.initSign(privateKey);
        s.update(data);

        byte[] signature = s.sign();
        String result = Base64.encodeToString(signature, Base64.DEFAULT);
        return result;
    }

    /**
     * public boolean verifyData
     *
     * This method is mostly used to debug the app during development to see if the digital signature
     * was working properly. The exclusion of this method should not affect the app's overall
     * functionality during app deployment.
     *
     * @param input
     * @param signatureStr
     * @return
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws UnrecoverableEntryException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public boolean verifyData(String input, String signatureStr) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableEntryException, InvalidKeyException, SignatureException {
        byte[] data = input.getBytes();
        byte[] signature;
        // BEGIN_INCLUDE(decode_signature)

        // Make sure the signature string exists.  If not, bail out, nothing to do.

        if (signatureStr == null) {
            Log.w(TAG, "Invalid signature.");
            Log.w(TAG, "Exiting verifyData()...");
            return false;
        }

        try {
            // The signature is going to be examined as a byte array,
            // not as a base64 encoded string.
            signature = Base64.decode(signatureStr, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            // signatureStr wasn't null, but might not have been encoded properly.
            // It's not a valid Base64 string.
            return false;
        }

        Signature s = Signature.getInstance(SecurityConstants.SIGNATURE_SHA256withRSA);

        s.initVerify(publicKey);
        s.update(data);
        boolean valid = s.verify(signature);
        return valid;
    }

    /**
     * public KeyPair setKeyPair
     *
     * This method is called when external Public and Private Key files are located locally in the
     * app's directory. When they exist, this method retrieves the files according to the Account
     * object, and stores them as part of a KeyPair object.
     *
     * @return
     * @throws InvalidKeySpecException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public KeyPair setKeyPair() throws InvalidKeySpecException,
            IOException, NoSuchAlgorithmException {

        File publicKey = new File(context.getExternalFilesDir(null), mAccount.getPublicKey());
        FileInputStream fis = new FileInputStream(publicKey.getAbsoluteFile());
        byte[] encodedPublicKey = new byte[(int)publicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        File privateKey = new File(context.getExternalFilesDir(null), mAccount.getPrivateKey());
        fis = new FileInputStream(privateKey.getAbsoluteFile());
        byte[] encodedPrivateKey = new byte[(int)privateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        KeyFactory kf = KeyFactory.getInstance(SecurityConstants.TYPE_RSA);

        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(encodedPublicKey);
        this.publicKey = kf.generatePublic(pubSpec);

        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        this.privateKey = kf.generatePrivate(privSpec);

        keys_loaded = true;

        return new KeyPair(this.publicKey, this.privateKey);
    }

    /**
     * public boolean areKeysLoaded
     *
     * A simple method to check and see if there are Public or Private keys already loaded.
     *
     * @return
     */
    public boolean areKeysLoaded() {
        return keys_loaded;
    }

    /**
     * public String encrypt
     *
     * This method, as opposed to the previous encrypt() method, utilizes AES encryption instead.
     * The method encrypts it according to our static IV and secret key, and then encodes it in Base64
     * before returning the encrypted values.
     *
     * In future iterations of this project, this method needs to be revised with a random IV instead
     * of a static one.
     *
     * TODO: Add randomized IV
     *
     * @param clear
     * @return
     * @throws Exception
     */
    public String encrypt(byte[] clear) throws Exception {
        SecretKeySpec sKeySpec = new SecretKeySpec(KEY, SecurityConstants.TYPE_AES);
        IvParameterSpec IVSpec = new IvParameterSpec(IV);
        Cipher cipher = Cipher.getInstance(SecurityConstants.TYPE_AES_CBC_PKCS5Padding);
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, IVSpec);
        byte[] encryptedBytes = cipher.doFinal(clear);

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    /**
     * public String decrypt
     *
     * This method, as opposed to the previous decrypt() method, utilizes AES decryption instead.
     * The method decrypts it according to our static IV and secret key, but firsts decodes it from
     * Base64 encoding.
     *
     * In future iterations of this project, this method needs to be revised to parse the first 16-bytes
     * to retrieve the random IV instead of using a static one.
     *
     * TODO: split the first 16-bytes of data to retrieve random IV
     *
     * @param encrypted
     * @return
     * @throws Exception
     */
    public String decrypt(byte[] encrypted) throws Exception {
        SecretKeySpec sKeySpec = new SecretKeySpec(KEY, SecurityConstants.TYPE_AES);
        IvParameterSpec IVSpec = new IvParameterSpec(IV);
        Cipher cipher = Cipher.getInstance(SecurityConstants.TYPE_AES_CBC_PKCS5Padding);
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, IVSpec);

        byte[] encryptedBytes = Base64.decode(encrypted, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decrypted = new String(decryptedBytes);
        return decrypted;
    }

    /**
     * public SecretKey generateAESKey
     *
     * Creates a key based on the number of bits provided.
     *
     * @param bits
     * @return
     * @throws NoSuchAlgorithmException
     */
    public SecretKey generateAESKey(int bits) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(SecurityConstants.TYPE_AES);
        keyGen.init(bits);
        return keyGen.generateKey();
    }
}
