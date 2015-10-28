package net.liveforcode.SSHProxySwitcher;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProfileManagerTest implements ProfileManager.ProfileListener {

    private final File xmlFile = new File("profiles.xml");
    private ProfileManager profileManager;

    @Before
    public void setUp() throws Exception {
        profileManager = new ProfileManager(xmlFile);
        assertNotNull("ProfileManager is null", profileManager);
        assertNotNull("profileListenerList is null", profileManager.profileListenerList);
        assertNotNull("xmlFile is null", profileManager.xmlFile);
        assertEquals("xmlFile is not the same as supplied", xmlFile, profileManager.xmlFile);
    }

    @Test
    public void testAddAndRemoveListener() throws Exception {
        profileManager.profileListenerList.clear();

        profileManager.addProfileListener(this);
        assertTrue("Profile Listener is not in list", profileManager.profileListenerList.contains(this));

        profileManager.removeProfileListener(this);
        assertFalse("Profile Listener is still in list", profileManager.profileListenerList.contains(this));
    }

    @Test
    public void testAddDuplicateListener() throws Exception {
        profileManager.profileListenerList.clear();

        profileManager.addProfileListener(this);
        assertTrue("Profile Listener is not in list", profileManager.profileListenerList.contains(this));

        profileManager.addProfileListener(this);
        assertEquals("Profile Listener List has too many items", 1, profileManager.profileListenerList.size());
    }

    @Test
    public void testRemoveNonAddedListener() throws Exception {
        profileManager.profileListenerList.clear();

        profileManager.removeProfileListener(this);
    }

    @Test
    public void testLoadProfilesFromXmlFile() throws Exception {
        Profile[] profiles = ProfileManager.loadProfilesFromXML(getClass().getResourceAsStream("/profiles.xml"));

        assertNotNull("Profiles array is null", profiles);
        assertEquals("Profiles array does not contain correct number of profiles", 2, profiles.length);

        Profile profile1 = profiles[0];
        assertEquals("Profile Name is incorrect", "Profile 1", profile1.getProfileName());
        assertEquals("SSH Host Address is incorrect", "test.com", profile1.getSshHostAddress());
        assertEquals("SSH Host Port is incorrect", 22, profile1.getSshHostPort());
        assertEquals("SSH Proxy Port is incorrect", 2000, profile1.getSshProxyPort());
        assertEquals("SSH Username is incorrect", "root", profile1.getSshUsername());
        assertEquals("SSH Password is incorrect", "password", profile1.getSshPassword());
        assertEquals("SSH Private Key is incorrect", new File("id_rsa"), profile1.getSshPrivateKey());

        Profile profile2 = profiles[1];
        assertEquals("Profile Name is incorrect", "Profile 2", profile2.getProfileName());
        assertEquals("SSH Host Address is incorrect", "test2.com", profile2.getSshHostAddress());
        assertEquals("SSH Host Port is incorrect", 2222, profile2.getSshHostPort());
        assertEquals("SSH Proxy Port is incorrect", 3000, profile2.getSshProxyPort());
        assertEquals("SSH Username is incorrect", "admin", profile2.getSshUsername());
        assertEquals("SSH Password is incorrect", "p@ssw0rd", profile2.getSshPassword());
        assertEquals("SSH Private Key is incorrect", new File("id_dsa"), profile2.getSshPrivateKey());
    }

    @Test
    public void testGetXmlFile() throws Exception {
        assertNotNull("xmlFile is null", profileManager.getXmlFile());
        assertEquals("xmlFile is not the same as supplied", xmlFile, profileManager.getXmlFile());
    }

    @Test
    public void testCreateProfilesXmlWhenNonExistent() throws Exception {
        if (xmlFile.exists())
            assertTrue("profiles.xml could not be deleted", xmlFile.delete());

        ProfileManager.createProfilesXml(xmlFile);
        assertTrue("profiles.xml was not created", xmlFile.exists());
    }

    @Test
    public void testCreateProfilesXmlWhenExistent() throws Exception {
        if (xmlFile.exists())
            assertTrue("profiles.xml could not be deleted", xmlFile.delete());

        ProfileManager.createProfilesXml(xmlFile);
        assertTrue("profiles.xml was not created", xmlFile.exists());

        long lastModified = xmlFile.lastModified();
        ProfileManager.createProfilesXml(xmlFile);
        assertEquals("profiles.xml was modified", lastModified, xmlFile.lastModified());
    }

    @Override
    public void onProfilesReloaded(ArrayList<Profile> loadedProfiles) {

    }

    @Override
    public void onProfileUpdated(Profile profile) {

    }

    @Override
    public void onProfileAdded(Profile profile) {

    }

    @Override
    public void onProfileRemoved(Profile profile) {

    }
}