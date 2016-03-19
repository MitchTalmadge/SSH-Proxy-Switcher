package com.mitchtalmadge.sshproxyswitcher.Managers2.PropertiesManager2;

public enum PropertiesEnum {

    TEST("Test", "test");

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
