package net.liveforcode.SSHProxySwitcher.Managers.LoggingManager;

public class LoggingException extends Exception {

    public LoggingException() {
    }

    public LoggingException(String message) {
        super(message);
    }

    public LoggingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoggingException(Throwable cause) {
        super(cause);
    }

    public LoggingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
