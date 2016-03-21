package com.mitchtalmadge.sshproxyswitcher.managers.properties;

public enum PropertiesEnum {

    MUTE_NOTIFICATIONS("Mute_Notifications", "false");

    private final String key;
    private final String defaultValue;

    PropertiesEnum(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
