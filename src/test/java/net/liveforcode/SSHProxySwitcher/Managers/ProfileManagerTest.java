package net.liveforcode.SSHProxySwitcher.Managers;

import net.liveforcode.SSHProxySwitcher.Profile;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProfileManagerTest {

    private static File xmlFile;
    private ProfileManager profileManager;

    @BeforeClass
    public static void initialize() throws Exception {
        xmlFile = new File("profiles.xml");
    }

    @Before
    public void setUp() throws Exception {
        this.profileManager = new ProfileManager();
        assertNotNull("ProfileManager is null", profileManager);
    }

    @Test
    public void testLoadProfilesWillNotCreateNewXMLFileIfValid() throws Exception {
        createValidMutableProfilesXmlFile();

        long modificationTimeBefore = xmlFile.lastModified();
        profileManager.loadProfilesFromXmlFile(xmlFile);
        long modificationTimeAfter = xmlFile.lastModified();
        assertEquals("XML file modification times differ!", modificationTimeBefore, modificationTimeAfter);
    }

    @Test
    public void testLoadProfilesWillCreateNewXMLFileCorrectlyIfInvalid() throws Exception {
        createInvalidMutableProfilesXmlFile();

        profileManager.loadProfilesFromXmlFile(xmlFile);
        assertTrue("ProfileManager did not create a new XML file.", xmlFile.exists());

        checkXmlFileValidity();
    }

    @Test
    public void testLoadProfilesWillCreateNewXMLFileCorrectlyIfNotExists() throws Exception {
        deleteMutableProfilesXmlFile();

        profileManager.loadProfilesFromXmlFile(xmlFile);
        assertTrue("ProfileManager did not create a new XML file.", xmlFile.exists());

        checkXmlFileValidity();
    }

    @Test
    public void testLoadProfilesWillLoadCorrectProfiles() throws Exception {
        createValidMutableProfilesXmlFile();

        profileManager.loadProfilesFromXmlFile(xmlFile);
        ArrayList<Profile> profiles = profileManager.getLoadedProfiles();

        assertNotNull("Profiles List is null", profiles);
        assertEquals("An incorrect number of profiles were loaded", 2, profiles.size());

        Profile profile = profiles.get(0);
        assertEquals("Profile Name is incorrect", "Profile 1", profile.getProfileName());
        assertEquals("SSH Host Address is incorrect", "test.com", profile.getSshHostAddress());
        assertEquals("SSH Host Port is incorrect", 22, profile.getSshHostPort());
        assertEquals("SSH Proxy Port is incorrect", 2000, profile.getSshProxyPort());
        assertEquals("SSH Username is incorrect", "root", profile.getSshUsername());
        assertEquals("SSH Password is incorrect", "password", profile.getSshPassword());
        assertEquals("SSH Private Key is incorrect", new File("id_rsa"), profile.getSshPrivateKey());
    }

    private void deleteMutableProfilesXmlFile() {
        if (xmlFile.exists())
            try {
                Files.delete(xmlFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        assertFalse("Old XML file still exists.", xmlFile.exists());
    }

    private void createInvalidMutableProfilesXmlFile() throws IOException {
        if (xmlFile.exists())
            deleteMutableProfilesXmlFile();
        Files.copy(getClass().getResourceAsStream("/invalidProfiles.xml"), xmlFile.toPath());
        assertTrue("XML file does not exist.", xmlFile.exists());
    }

    private void createValidMutableProfilesXmlFile() throws IOException {
        if (xmlFile.exists())
            deleteMutableProfilesXmlFile();
        Files.copy(getClass().getResourceAsStream("/validProfiles.xml"), xmlFile.toPath());
        assertTrue("XML file does not exist.", xmlFile.exists());
    }

    private void checkXmlFileValidity() {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            documentBuilder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) throws SAXException {
                    ;
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    throw e;
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    throw e;
                }
            });
            Document document = documentBuilder.parse(xmlFile);

            assertTrue("ProfileManager did not create the XML file correctly.", document.getDocumentElement().getTagName().equals("Profiles"));
        } catch (SAXParseException e) {
            fail("Exception thrown while trying to parse XML file:\n" + e.toString());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}