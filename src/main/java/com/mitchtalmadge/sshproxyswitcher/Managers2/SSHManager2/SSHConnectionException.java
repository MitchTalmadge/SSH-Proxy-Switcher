package com.mitchtalmadge.sshproxyswitcher.Managers2.SSHManager2;

public class SSHConnectionException extends Exception {

    public SSHConnectionException() {
    }

    public SSHConnectionException(String message) {
        super(message);
    }

    public SSHConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SSHConnectionException(Throwable cause) {
        super(cause);
    }

    public SSHConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
