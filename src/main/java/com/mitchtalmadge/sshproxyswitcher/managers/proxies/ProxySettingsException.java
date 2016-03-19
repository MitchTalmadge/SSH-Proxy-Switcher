package com.mitchtalmadge.sshproxyswitcher.managers.proxies;

public class ProxySettingsException extends Exception {

    public ProxySettingsException() {
    }

    public ProxySettingsException(String message) {
        super(message);
    }

    public ProxySettingsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxySettingsException(Throwable cause) {
        super(cause);
    }

    public ProxySettingsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
