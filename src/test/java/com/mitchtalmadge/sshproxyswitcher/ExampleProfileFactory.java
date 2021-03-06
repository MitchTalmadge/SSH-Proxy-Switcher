package com.mitchtalmadge.sshproxyswitcher;

import com.mitchtalmadge.sshproxyswitcher.managers.profiles.Profile;

public class ExampleProfileFactory {

    public static final String CORRECT_HOST = "localhost";
    public static final String INCORRECT_HOST = "incorrect.host";
    public static final int CORRECT_HOST_PORT = 50000;
    public static final String USERNAME = "tester";
    public static final String CORRECT_PASSWORD = "correctPassword";
    public static final String INCORRECT_PASSWORD = "incorrectPassword";

    private static final Profile VALID_PROFILE;
    private static final Profile INVALID_AUTH_PROFILE;
    private static final Profile INVALID_HOST_PROFILE;
    private static final Profile INVALID_PROXY_PROFILE;

    static {
        VALID_PROFILE = new Profile("Valid Profile");
        VALID_PROFILE.setSshHostName(CORRECT_HOST);
        VALID_PROFILE.setSshHostPort(CORRECT_HOST_PORT);
        VALID_PROFILE.setSshUsername(USERNAME);
        VALID_PROFILE.setSshPassword(CORRECT_PASSWORD);
        VALID_PROFILE.setConnectToSsh(true);
        VALID_PROFILE.setUseSshDynamicTunnel(true);
        VALID_PROFILE.setProxyPort(50001);

        INVALID_AUTH_PROFILE = VALID_PROFILE.clone();
        INVALID_AUTH_PROFILE.setProfileName("Invalid Profile");
        INVALID_AUTH_PROFILE.setSshPassword(INCORRECT_PASSWORD);

        INVALID_HOST_PROFILE = VALID_PROFILE.clone();
        INVALID_HOST_PROFILE.setProfileName("Invalid Host Profile");
        INVALID_HOST_PROFILE.setSshHostName(INCORRECT_HOST);

        INVALID_PROXY_PROFILE = VALID_PROFILE.clone();
        INVALID_PROXY_PROFILE.setProfileName("Invalid Proxy Profile");
        INVALID_PROXY_PROFILE.setProxyPort(-1);
    }

    public static Profile getValidProfile() {
        return VALID_PROFILE.clone();
    }

    public static Profile getInvalidAuthProfile() {
        return INVALID_AUTH_PROFILE.clone();
    }

    public static Profile getInvalidHostProfile() {
        return INVALID_HOST_PROFILE.clone();
    }

    public static Profile getInvalidProxyProfile() {
        return INVALID_PROXY_PROFILE.clone();
    }
}
