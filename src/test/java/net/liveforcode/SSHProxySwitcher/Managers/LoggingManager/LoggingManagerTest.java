package net.liveforcode.SSHProxySwitcher.Managers.LoggingManager;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static org.junit.Assert.*;

public class LoggingManagerTest {

    private static final File LOG_DIR = new File("logs/");
    private LoggingManager loggingManager;

    @BeforeClass
    public static void beforeClass()
    {
        if(!LOG_DIR.exists())
            LOG_DIR.mkdir();
    }

    @Before
    public void setUp() throws Exception {
        this.loggingManager = new LoggingManager();
        assertNotNull("LoggingManager is null", loggingManager);
        clearLogDirectory();
    }

    @After
    public void tearDown() throws Exception {
        loggingManager.stopLogging();
        clearLogDirectory();
    }

    @Test
    public void testStartLoggingWillRotateLogFiles() throws Exception {
        clearLogDirectory();
        for (int i = 0; i < 15; i++) {
            this.loggingManager.startLogging(LOG_DIR);
            loggingManager.stopLogging();
        }
        assertEquals("There is an incorrect number of log files in the Log Directory!", 10, LOG_DIR.listFiles().length);
    }

    private void clearLogDirectory() {
        assertNotNull("Could not list files of Log Directory", LOG_DIR.listFiles());
        for (File file : LOG_DIR.listFiles())
            assertTrue("Could not delete log file " + file.getName(), file.delete());
        assertEquals("There are still log files in the Log Directory!", 0, LOG_DIR.listFiles().length);
    }

    @Test
    public void testLoggerWillWriteToFile() throws Exception {
        clearLogDirectory();
        loggingManager.startLogging(LOG_DIR);

        LinkedHashMap<Level, String> logMap = new LinkedHashMap<>();
        logMap.put(Level.INFO, "Test Info");
        logMap.put(Level.WARNING, "Test Warning");
        logMap.put(Level.SEVERE, "Test Severe");

        Set<Map.Entry<Level, String>> entries = logMap.entrySet();
        for (Map.Entry<Level, String> entry : entries) {
            loggingManager.log(entry.getKey(), entry.getValue());
        }

        loggingManager.stopLogging();

        File[] logFiles = LOG_DIR.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("0.0.log");
            }
        });
        assertEquals("An unexpected number of log files were found!", 1, logFiles.length);

        File logFile = logFiles[0];
        FileInputStream inputStream = new FileInputStream(logFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        for (Map.Entry<Level, String> entry : entries) {
            String line = bufferedReader.readLine();
            assertNotNull("No more lines to read!", line);

            assertTrue("Line does not contain Log Level", line.contains(entry.getKey().getName()));
            assertTrue("Line does not contain Log Message", line.contains(entry.getValue()));
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
    }
}