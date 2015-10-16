/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.cardemulation;

import android.content.Context;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;

import com.example.android.common.logger.Log;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.SecretKey;

/**
 * This is a APDU Service which interfaces with the card emulation support added in Android 4.4,
 * KitKat.
 *
 * This application will be invoked for any terminals selecting AID 0xF22222222.
 *
 * Note: This is a low-level interface. Unlike the NdefMessage many developers
 * are familiar with for implementing Android Beam in apps, card emulation only provides a
 * byte-array based communication channel. It is left to developers to implement higher level
 * protocol support as needed.
 *
 *       Methods:
 *              public void onDeactivated(int reason)
 *              public byte[] processCommandApdu(byte[] commandApdu, Bundle extras)
 *              public static byte[] BuildSelectApdu(String aid)
 *              public static String ByteArrayToHexString(byte[] bytes)
 *              public static byte[] HexStringToByteArray(String s)
 *              public static byte[] ConcatArrays(byte[] first, byte[]... rest)
 *
 *       Authors: Android Developers
 *       Modified by: David Tran
 *       Date Modified: March 9, 2015
 *
 */
public class CardService extends HostApduService {
    private static final String TAG = "CardService";

    // AID for our loyalty card service.
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";

    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
    private static final String SELECT_APDU_HEADER = "00A40400";

    // "OK" status word sent in response to SELECT AID command (0x9000)
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("");

    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("");

    private static final byte[] SELECT_APDU = BuildSelectApdu(SAMPLE_LOYALTY_CARD_AID);

    private Context context = this;
    private PublicKey pubKey = null;
    private PrivateKey privKey = null;
    private SecretKey mSecretKey = null;
    private KeyPair kp = null;

    private AccountsDataSource dataSource = null;
    private String mSignatureStr = "";


    /**
     * Called if the connection to the NFC card is lost, in order to let the application know the
     * cause for the disconnection (either a lost link, or another AID being selected by the
     * reader).
     *
     * EDIT: There doesn't seem to be a reason during this iteration of the project to implement
     * any logic for this method. Perhaps in the future, this method could be utilized to
     * troubleshoot problems with the Smartphone and the NFC reader.
     *          - DT
     *
     * @param reason Either DEACTIVATION_LINK_LOSS or DEACTIVATION_DESELECTED
     */
    @Override
    public void onDeactivated(int reason) { }

    /**
     * This method will be called when a command APDU has been received from a remote device. A
     * response APDU can be provided directly by returning a byte-array in this method. In general
     * response APDUs must be sent as quickly as possible, given the fact that the user is likely
     * holding his device over an NFC reader when this method is called.
     *
     * EDIT: We haven't ran into any major problems or inconsistencies with the NFC reader or the
     * Smartphone, even with the new implementation of generating keys, loading keys, encryption
     * and decryption. This may be due to the Samsung Note 4 being such a strong "superphone", and
     * having a high processing power. It may not be the same case when it comes to other older
     * smartphones.
     *          - DT
     *
     * If there are multiple services that have registered for the same AIDs in
     * their meta-data entry, you will only get called if the user has explicitly selected your
     * service, either as a default or just for the next tap.
     *
     * @param commandApdu The APDU that received from the remote device
     * @param extras A bundle containing extra data. May be null.
     * @return a byte-array containing the response APDU, or null if no response APDU can be sent
     * at this point.
     */
    // BEGIN_INCLUDE(processCommandApdu)
    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.i(TAG, "Received APDU: " + ByteArrayToHexString(commandApdu));
        // If the APDU matches the SELECT AID command for this service,
        if (Arrays.equals(SELECT_APDU, commandApdu)) {
            // Initialize the database and get the account.
            dataSource = new AccountsDataSource(context);
            dataSource.open();
            dataSource.initDatabase();

            Account accountObj = dataSource.getAccount(AccountStorage.GetUsername(context), AccountStorage.GetAccount(context));
            dataSource.close();

            if (accountObj == null) {
                Log.e(TAG, "No such account retrieved from AccountDataSource: " +
                        AccountStorage.GetUsername(context) + " " + AccountStorage.GetAccount(context));
                return UNKNOWN_CMD_SW;
            }

            String account = accountObj.getUsername() + " " + accountObj.getAccount();

            KeyManagement keyManager = null;
            try {
                keyManager = new KeyManagement(context, "CardEmulation", accountObj);
                mSecretKey = keyManager.generateAESKey(128);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            String encryptedData = "";
            String decryptedData = "";

            File publicKey = new File(context.getExternalFilesDir(null), accountObj.getPublicKey());
            File privateKey = new File(context.getExternalFilesDir(null), accountObj.getPrivateKey());

            try {
                if (!publicKey.exists() || !privateKey.exists()) {
                    boolean loaded = keyManager.createKeys();
                    Log.i(TAG, "Keys loaded successfully?: " + loaded);
                }

                kp = keyManager.setKeyPair();

                if (keyManager.areKeysLoaded() == false) {
                    Log.e(TAG, "Bad Keys Loaded...");
                    return UNKNOWN_CMD_SW;
                }

                pubKey = kp.getPublic();
                privKey = kp.getPrivate();

                mSignatureStr = keyManager.signData(account);
                Log.i(TAG, "Signature: " + mSignatureStr.toString());

                boolean verified = keyManager.verifyData(account, mSignatureStr);
                Log.i(TAG, "Verified?: " + verified);

            } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException |
                    InvalidKeyException | InvalidAlgorithmParameterException | NoSuchProviderException |
                    SignatureException | CertificateException | UnrecoverableEntryException |
                    KeyStoreException e) {
                e.printStackTrace();
            }

            try {
                encryptedData = keyManager.encrypt(account.getBytes());
                decryptedData = keyManager.decrypt(encryptedData.getBytes());
                encryptedData += "::" + mSignatureStr;
            } catch (Exception e) {
                e.printStackTrace();
            }

            byte[] encryptedBytes = encryptedData.getBytes();

            Log.i(TAG, "Encrypted Stuff: " + encryptedData);
            Log.i(TAG, "Size of Stuff: " + account.length());
            Log.i(TAG, "Size of Encrypted Stuff: " + encryptedData.length());

            // Decrypting, just for fun :)
            Log.i(TAG, "Decrypted Values: " + decryptedData);

            return ConcatArrays(encryptedBytes, SELECT_OK_SW);
        } else {
            return UNKNOWN_CMD_SW;
        }
    }

    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    public static byte[] BuildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X",
                aid.length() / 2) + aid);
    }

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
