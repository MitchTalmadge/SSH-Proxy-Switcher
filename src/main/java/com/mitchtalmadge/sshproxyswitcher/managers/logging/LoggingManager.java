package com.mitchtalmadge.sshproxyswitcher.managers.logging;

import com.mitchtalmadge.sshproxyswitcher.Versioning;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;

public class LoggingManager {

    private static final int LOG_SIZE = 10240;
    private static final int LOG_ROTATION_COUNT = 10;
    private static final String LOG_FORMAT = Versioning.PROGRAM_NAME_NO_SPACES + ".%u.%g.log";

    private final Logger logger = Logger.getLogger(Versioning.PROGRAM_NAME_WITH_VERSION);
    private FileHandler fileHandler;

    private ArrayList<LogListener> logListeners;

    public void startLogging(File logDir) throws LoggingException {
        if (!logDir.exists())
            if (!logDir.mkdir())
                throw new LoggingException("Could not create log directory!");

        try {
            logListeners = new ArrayList<>();

            fileHandler = new FileHandler(logDir.getAbsolutePath() + "/" + LOG_FORMAT, LOG_SIZE, LOG_ROTATION_COUNT);
            SimpleLogFormatter simpleLogFormatter = new SimpleLogFormatter();
            fileHandler.setFormatter(new SimpleLogFormatter());
            logger.setLevel(Level.FINE);
            logger.addHandler(fileHandler);
            logger.addHandler(new Handler() {
                @Override
                public void publish(LogRecord record) {
                    if (logListeners != null) {
                        for (LogListener logListener : logListeners) {
                            logListener.log(record.getLevel(), simpleLogFormatter.format(record));
                        }
                    }
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() throws SecurityException {
                }
            });
        } catch (IOException e) {
            throw new LoggingException(e);
        }
    }

    public void stopLogging() {
        if (fileHandler != null) {
            fileHandler.close();
            fileHandler = null;
        }
    }

    public void log(Level logLevel, String message) {
        logger.log(logLevel, message);
    }

    public void addLogListener(LogListener listener) {
        logListeners.add(listener);
    }

    public interface LogListener {
        void log(Level logLevel, String message);
    }

    private class SimpleLogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
            String date = simpleDateFormat.format(new Date());
            return date + " [" + record.getLevel().getName() + "]\t" + (record.getLevel().equals(Level.INFO) || record.getLevel().equals(Level.FINE) ? "\t" : "") + record.getMessage() + "\n";
        }
    }

}
