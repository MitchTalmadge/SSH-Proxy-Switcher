package com.mitchtalmadge.sshproxyswitcher.managers.profiles;

import com.mitchtalmadge.sshproxyswitcher.utilities.Encryptable;
import com.mitchtalmadge.sshproxyswitcher.utilities.EncryptionHelper;

import java.lang.reflect.Field;

public class ProfileEncryptionAdapter {

    private static final String SALT = "SSHPT_SALT";

    public static Profile encryptProfile(Profile unEncryptedProfile) throws ProfileCryptException {
        if (unEncryptedProfile.encrypted) {
            throw new ProfileCryptException("Profile is already encrypted.");
        }

        try {
            Profile encryptedProfile = unEncryptedProfile.clone();

            for (Field field : Profile.class.getDeclaredFields()) {
                if (!field.isAccessible())
                    field.setAccessible(true);

                if (field.isAnnotationPresent(Encryptable.class)) {
                    Object fieldObj = field.get(encryptedProfile);
                    if (fieldObj instanceof String) {
                        field.set(encryptedProfile, EncryptionHelper.encryptText((String) fieldObj, encryptedProfile.getProfileName() + SALT));
                    }
                }
            }
            encryptedProfile.encrypted = true;

            return encryptedProfile;
        } catch (IllegalAccessException e) {
            throw new ProfileCryptException(e.getMessage());
        }
    }

    public static Profile decryptProfile(Profile encryptedProfile) throws ProfileCryptException {
        if (!encryptedProfile.encrypted) {
            throw new ProfileCryptException("Profile is not encrypted.");
        }

        try {
            Profile decryptedProfile = encryptedProfile.clone();

            for (Field field : Profile.class.getDeclaredFields()) {
                if (!field.isAccessible())
                    field.setAccessible(true);

                if (field.isAnnotationPresent(Encryptable.class)) {
                    Object fieldObj = field.get(decryptedProfile);
                    if (fieldObj instanceof String) {
                        field.set(decryptedProfile, EncryptionHelper.decryptText((String) fieldObj, decryptedProfile.getProfileName() + SALT));
                    }
                }
            }
            decryptedProfile.encrypted = false;

            return decryptedProfile;
        } catch (IllegalAccessException e) {
            throw new ProfileCryptException(e.getMessage());
        }
    }

}
