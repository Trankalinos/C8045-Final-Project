package com.example.android.cardemulation;

/**
 * A typical Java Bean class that creates an Account object for ease of storage and access.
 * Each String parameter has a getter and setter. This class doesn't have any special logic or
 * method implemented during this iteration.
 *
 *      Classes:
 *          public Account() // Default constructor
 *          public Account(String username, String account, String secretKey...) // Other constructor
 *
 *      Methods:
 *          public String getUsername()
 *          public void setUsername(String username)
 *          ...
 *
 *      Author: David Tran
 *      Created on: March 9, 2015
 */
public class Account {
    private String username = "";
    private String account = "";
    private String secretKey = "";
    private String iv = "";
    private String publicKey = "";
    private String privateKey = "";

    public Account() {
        setUsername("Trankalinos");
        setAccount("712052");
        setSecretKey("This is a key123");
        setIv("This is an IV456");
        setPublicKey("public-key.txt");
        setPrivateKey("private-key.txt");
    }

    public Account(String username, String account, String secretKey,
                   String iv, String publicKey, String privateKey) {
        this.setUsername(username);
        this.setAccount(account);
        this.setSecretKey(secretKey);
        this.setIv(iv);
        this.setPublicKey(publicKey);
        this.setPrivateKey(privateKey);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
