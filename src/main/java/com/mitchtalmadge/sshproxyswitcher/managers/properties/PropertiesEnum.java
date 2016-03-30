package com.mitchtalmadge.sshproxyswitcher.managers.properties;

public enum PropertiesEnum {

    NOTIFY_CONNECT("notify_connect", "true"),
    NOTIFY_CONNECT_FAIL("notify_connect_fail", "true"),
    NOTIFY_CONNECT_LOST("notify_connect_lost", "true"),
    NOTIFY_DISCONNECT("notify_disconnect", "true"),
    NOTIFY_RECONNECT("notify_reconnect", "true"),
    NOTIFY_RECONNECT_FAIL("notify_reconnect_fail", "true"),

    START_WITH_WINDOWS("start_with_windows", "false"),
    AUTO_CONNECT_PROFILE("auto_connect_profile", "");

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
