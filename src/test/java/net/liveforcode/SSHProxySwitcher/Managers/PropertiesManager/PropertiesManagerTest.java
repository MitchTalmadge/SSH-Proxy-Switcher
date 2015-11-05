package net.liveforcode.SSHProxySwitcher.Managers.PropertiesManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import static org.junit.Assert.*;

public class PropertiesManagerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private PropertiesManager propertiesManager;
    private File propertiesFile = new File("SSHProxySwitcher.config");
    private FileInputStream inputStream;
    private FileOutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        this.propertiesManager = new PropertiesManager();
        assertNotNull("PropertiesManager is null", propertiesManager);
    }

    @After
    public void tearDown() throws Exception {
        if (inputStream != null)
            inputStream.close();
        if (outputStream != null)
            outputStream.close();
    }

    @Test
    public void testLoadPropertiesWillCreateFileIfNotExists() throws Exception {
        deletePropertiesFile();
        propertiesManager.loadPropertiesFromFile(propertiesFile);
        assertTrue("Properties File was not created", propertiesFile.exists());
    }

    @Test
    public void testLoadPropertiesWillValidateInvalidPropertiesFile() throws Exception {
        deletePropertiesFile();
        assertTrue("Could not create Properties File", propertiesFile.createNewFile());

        Properties properties = getLoadedProperties(propertiesFile);
        for (PropertiesEnum enom : PropertiesEnum.values()) {
            properties.setProperty(enom.getKey(), "");
        }
        saveProperties(properties, propertiesFile);
        assertTrue("Properties File does not exist", propertiesFile.exists());

        propertiesManager.loadPropertiesFromFile(propertiesFile);

        properties = getLoadedProperties(propertiesFile);
        for (PropertiesEnum enom : PropertiesEnum.values()) {
            assertEquals("Properties File is invalid", enom.getDefaultValue(), properties.getProperty(enom.getKey()));
        }
    }

    @Test
    public void testLoadPropertiesWillNotModifyValidPropertiesFile() throws Exception {
        deletePropertiesFile();
        assertTrue("Could not create Properties File", propertiesFile.createNewFile());

        Properties properties = getLoadedProperties(propertiesFile);
        for (PropertiesEnum enom : PropertiesEnum.values()) {
            properties.setProperty(enom.getKey(), enom.getDefaultValue());
        }
        saveProperties(properties, propertiesFile);
        assertTrue("Properties File does not exist", propertiesFile.exists());

        long lastModified = propertiesFile.lastModified();
        propertiesManager.loadPropertiesFromFile(propertiesFile);
        assertEquals("Properties File was modified", lastModified, propertiesFile.lastModified());
    }

    @Test
    public void testLoadPropertiesWillThrowPropertiesExceptionIfFileIsNull() throws Exception {
        expectedException.expect(PropertiesException.class);
        expectedException.expectMessage("Properties File is null");
        propertiesManager.loadPropertiesFromFile(null);
    }

    private void deletePropertiesFile() {
        File propertiesFile = new File("SSHProxySwitcher.config");
        if (propertiesFile.exists())
            assertTrue("Properties File could not be deleted", propertiesFile.delete());
    }

    private Properties getLoadedProperties(File propertiesFile) throws Exception {
        inputStream = new FileInputStream(propertiesFile);
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

    private void saveProperties(Properties properties, File propertiesFile) throws Exception {
        outputStream = new FileOutputStream(propertiesFile);
        properties.store(outputStream, null);
        outputStream.close();
    }
}