package com.mitchtalmadge.sshproxyswitcher.utilities;

import org.jasypt.util.text.StrongTextEncryptor;

public class EncryptionHelper {

    public static String encryptText(String text, String password)
    {
        StrongTextEncryptor strongTextEncryptor = new StrongTextEncryptor();
        strongTextEncryptor.setPassword(password);

        return strongTextEncryptor.encrypt(text);
    }

    public static String decryptText(String encryptedText, String password)
    {
        StrongTextEncryptor strongTextEncryptor = new StrongTextEncryptor();
        strongTextEncryptor.setPassword(password);

        return strongTextEncryptor.decrypt(encryptedText);
    }

}
