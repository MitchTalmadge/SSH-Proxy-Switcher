package com.mitchtalmadge.sshproxyswitcher.utilities;

import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptionHelper {

    public static String encryptText(String text, String password)
    {
        BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword(password);

        return basicTextEncryptor.encrypt(text);
    }

    public static String decryptText(String encryptedText, String password)
    {
        BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword(password);

        return basicTextEncryptor.decrypt(encryptedText);
    }

}
