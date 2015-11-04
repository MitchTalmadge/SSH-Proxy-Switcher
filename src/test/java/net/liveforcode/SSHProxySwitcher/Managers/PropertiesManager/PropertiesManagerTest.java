package net.liveforcode.SSHProxySwitcher.Managers.PropertiesManager;

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

    private PropertiesManager propertiesManager;
    private File propertiesFile = new File("SSHProxySwitcher.config");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        this.propertiesManager = new PropertiesManager();
        assertNotNull("PropertiesManager is null", propertiesManager);
    }

    @Test
    public void testLoadPropertiesWillCreateFileIfNotExists() throws Exception {
        deletePropertiesFile();
        propertiesManager.loadPropertiesFromFile(propertiesFile);
        assertTrue("Properties File does not exist", propertiesFile.exists());
    }

    @Test
    public void testLoadPropertiesWillValidateInvalidPropertiesFile() throws Exception {
        deletePropertiesFile();
        assertTrue("Could not create Properties File", propertiesFile.createNewFile());

        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        for(PropertiesEnum enom : PropertiesEnum.values())
        {
            properties.setProperty(enom.getKey(), "");
        }
        properties.store(new FileOutputStream(propertiesFile), null);
        assertTrue("Properties File does not exist", propertiesFile.exists());

        propertiesManager.loadPropertiesFromFile(propertiesFile);

        properties.load(new FileInputStream(propertiesFile));
        for(PropertiesEnum enom : PropertiesEnum.values())
        {
            assertEquals("Properties File is invalid", enom.getDefaultValue(), properties.getProperty(enom.getKey()));
        }
    }

    @Test
    public void testLoadPropertiesWillNotModifyValidPropertiesFile() throws Exception {
        deletePropertiesFile();
        assertTrue("Could not create Properties File", propertiesFile.createNewFile());

        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        for(PropertiesEnum enom : PropertiesEnum.values())
        {
            properties.setProperty(enom.getKey(), enom.getDefaultValue());
        }
        properties.store(new FileOutputStream(propertiesFile), null);
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

    private void deletePropertiesFile()
    {
        File propertiesFile = new File("SSHProxySwitcher.config");
        if(propertiesFile.exists())
            assertTrue("Properties File could not be deleted", propertiesFile.delete());
    }
}