package net.liveforcode.SSHProxySwitcher.Managers.LoggingManager;

import net.liveforcode.SSHProxySwitcher.Versioning;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LoggingManager {

    private static final int LOG_SIZE = 10240;
    private static final int LOG_ROTATION_COUNT = 10;
    private static final String LOG_FORMAT = Versioning.PROGRAM_NAME_NO_SPACES + ".%u.%g.log";

    private final Logger logger = Logger.getLogger(Versioning.PROGRAM_NAME_WITH_VERSION);
    private FileHandler fileHandler;

    public void startLogging(File logDir) throws LoggingException {
        if (!logDir.exists())
            if (!logDir.mkdir())
                throw new LoggingException("Could not create log directory!");

        try {
            fileHandler = new FileHandler(logDir.getAbsolutePath() + "/" + LOG_FORMAT, LOG_SIZE, LOG_ROTATION_COUNT);
            fileHandler.setFormatter(new SimpleLogFormatter());
            logger.addHandler(fileHandler);
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

    private class SimpleLogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy hh:mm:ss a");
            String date = simpleDateFormat.format(new Date());
            return date + " [" + record.getLevel().getName() + "]\t" + (record.getLevel().equals(Level.INFO) ? "\t" : "") + record.getMessage() + "\n";
        }
    }
}
