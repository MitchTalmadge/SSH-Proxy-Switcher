package net.liveforcode.SSHProxySwitcher;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProfileManagerTest implements ProfileManager.ProfileListener {

    private static File internalXmlFile;
    private static File externalXmlFile;
    private ProfileManager profileManager;

    @BeforeClass
    public static void initialize() throws Exception {
        try {
            internalXmlFile = new File(ProfileManagerTest.class.getResource("/profiles.xml").toURI());
            externalXmlFile = new File("profiles.xml");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        profileManager = new ProfileManager(externalXmlFile);
        assertNotNull("ProfileManager is null", profileManager);
        assertNotNull("profileListenerList is null", profileManager.profileListenerList);
    }

    @Test
    public void testAddAndRemoveListener() throws Exception {
        profileManager.addProfileListener(this);
        assertTrue("Profile Listener is not in list", profileManager.profileListenerList.contains(this));

        profileManager.removeProfileListener(this);
        assertFalse("Profile Listener is still in list", profileManager.profileListenerList.contains(this));
    }

    @Test
    public void testAddDuplicateListener() throws Exception {
        profileManager.addProfileListener(this);
        assertTrue("Profile Listener is not in list", profileManager.profileListenerList.contains(this));

        profileManager.addProfileListener(this);
        assertEquals("Profile Listener List has too many items", 1, profileManager.profileListenerList.size());
    }

    @Test
    public void testRemoveNonAddedListener() throws Exception {
        profileManager.removeProfileListener(this);
    }

    @Test
    public void testLoadProfilesFromXml_FileExists() throws Exception {
        profileManager = new ProfileManager(internalXmlFile);
        profileManager.loadProfilesFromXML();
        ArrayList<Profile> profilesList = profileManager.loadedProfiles;
        Profile[] profiles = profilesList.toArray(new Profile[profilesList.size()]);

        assertNotNull("Profiles array is null", profiles);
        assertEquals("Profiles array does not contain correct number of profiles", 2, profiles.length);

        assertEquals("Profiles ArrayLists do not match", profilesList, profileManager.getLoadedProfiles());
        assertArrayEquals("Profiles Arrays do not match", profiles, profileManager.getLoadedProfilesAsArray());

        Profile profile = profiles[0];
        assertEquals("Profile Name is incorrect", "Profile 1", profile.getProfileName());
        assertEquals("SSH Host Address is incorrect", "test.com", profile.getSshHostAddress());
        assertEquals("SSH Host Port is incorrect", 22, profile.getSshHostPort());
        assertEquals("SSH Proxy Port is incorrect", 2000, profile.getSshProxyPort());
        assertEquals("SSH Username is incorrect", "root", profile.getSshUsername());
        assertEquals("SSH Password is incorrect", "password", profile.getSshPassword());
        assertEquals("SSH Private Key is incorrect", new File("id_rsa"), profile.getSshPrivateKey());
    }

    @Test
    public void testLoadProfilesFromXml_FileNonExistent() throws Exception {
        if (externalXmlFile.exists())
            assertTrue("profiles.xml could not be deleted", externalXmlFile.delete());

        profileManager.loadProfilesFromXML();
        Profile[] profiles = profileManager.loadedProfiles.toArray(new Profile[profileManager.loadedProfiles.size()]);

        assertNotNull("Profiles array is null", profiles);
        assertTrue("profiles.xml was not created", externalXmlFile.exists());
        assertEquals("Profiles array does not contain correct number of profiles", 0, profiles.length);
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