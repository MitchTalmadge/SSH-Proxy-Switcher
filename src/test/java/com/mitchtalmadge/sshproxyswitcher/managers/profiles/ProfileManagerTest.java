package com.mitchtalmadge.sshproxyswitcher.managers.profiles;

import com.mitchtalmadge.sshproxyswitcher.ExampleProfileFactory;
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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProfileManagerTest {

    private ProfileManager profileManager;

    @BeforeClass
    public static void initialize() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        this.profileManager = new ProfileManager();
        assertNotNull("ProfileManager is null", profileManager);
    }

    @Test
    public void testProfilesAreCorrectlySavedAndLoaded() throws Exception {

        if(ProfileManager.profilesFile.exists())
            assertTrue("Could not delete Serialized Profiles File", ProfileManager.profilesFile.delete());

        Profile testProfile1 = ExampleProfileFactory.getValidProfile();
        testProfile1.setProfileName("Test Profile 1");
        Profile testProfile2 = ExampleProfileFactory.getValidProfile();
        testProfile2.setProfileName("Test Profile 2");

        profileManager.loadedProfiles = new ArrayList<>();
        profileManager.loadedProfiles.add(testProfile1);
        profileManager.loadedProfiles.add(testProfile2);

        profileManager.saveProfiles();

        assertTrue("Serialized Profiles File does not exist!", ProfileManager.profilesFile.exists());

        profileManager.loadProfiles();

        assertTrue("The number of profiles loaded was not 2!", profileManager.loadedProfiles.size() == 2);

        assertTrue("The first profile name is invalid!", profileManager.loadedProfiles.get(0).getProfileName().equals(testProfile1.getProfileName()));
        assertTrue("The second profile name is invalid!", profileManager.loadedProfiles.get(1).getProfileName().equals(testProfile2.getProfileName()));
    }
}