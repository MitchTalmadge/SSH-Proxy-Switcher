package com.mitchtalmadge.sshproxyswitcher.managers.profiles;

import com.mitchtalmadge.sshproxyswitcher.utilities.Encryptable;

import java.lang.reflect.Field;

public class ProfileEncryptionAdapter {

    public static Profile encryptProfile(Profile unEncryptedProfile) throws ProfileCryptException {
        if (unEncryptedProfile.encrypted) {
            throw new ProfileCryptException("Profile is already encrypted.");
        }

        try {
            Profile encryptedProfile = (Profile) unEncryptedProfile.clone();

            for (Field field : Profile.class.getDeclaredFields()) {
                if (!field.isAccessible())
                    field.setAccessible(true);

                field.set(encryptedProfile, field.get(unEncryptedProfile));

                if (field.isAnnotationPresent(Encryptable.class)) {
                    if(field.get(encryptedProfile) instanceof String)
                    {

                    }
                }
            }
            encryptedProfile.encrypted = true;

            return encryptedProfile;
        } catch (IllegalAccessException | CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

}
