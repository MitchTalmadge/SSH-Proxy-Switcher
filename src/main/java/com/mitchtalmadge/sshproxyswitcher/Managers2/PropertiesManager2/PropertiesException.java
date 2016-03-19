package com.mitchtalmadge.sshproxyswitcher.Managers2.PropertiesManager2;

public class PropertiesException extends Exception {

    public PropertiesException() {
    }

    public PropertiesException(String message) {
        super(message);
    }

    public PropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesException(Throwable cause) {
        super(cause);
    }

    public PropertiesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
