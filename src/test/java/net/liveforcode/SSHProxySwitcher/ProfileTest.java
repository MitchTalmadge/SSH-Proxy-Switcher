package net.liveforcode.SSHProxySwitcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ProfileTest {

    String profileName = "Test Profile";
    //SSH Settings:
    String sshHostAddress = "test.com";
    int sshHostPort = 28;
    int sshProxyPort = 2000;
    String sshUsername = "root";
    String sshPassword = "password";
    File sshPrivateKey = new File("id_rsa");

    @Test
    public void testCreateProfileWithoutArguments() throws Exception {
        Profile profile = new Profile();
        assertNotNull("Profile is null", profile);

        profile.setProfileName(profileName);
        profile.setSshHostAddress(sshHostAddress);
        profile.setSshHostPort(sshHostPort);
        profile.setSshProxyPort(sshProxyPort);
        profile.setSshUsername(sshUsername);
        profile.setSshPassword(sshPassword);
        profile.setSshPrivateKey(sshPrivateKey);

        assertEquals("Profile Name is incorrect", profileName, profile.getProfileName());
        assertEquals("SSH Host Address is incorrect", sshHostAddress, profile.getSshHostAddress());
        assertEquals("SSH Host Port is incorrect", sshHostPort, profile.getSshHostPort());
        assertEquals("SSH Proxy Port is incorrect", sshProxyPort, profile.getSshProxyPort());
        assertEquals("SSH Username is incorrect", sshUsername, profile.getSshUsername());
        assertEquals("SSH Password is incorrect", sshPassword, profile.getSshPassword());
        assertEquals("SSH Private Key is incorrect", sshPrivateKey, profile.getSshPrivateKey());
    }

    @Test
    public void testCreateProfileWithArguments() throws Exception {
        Profile profile = new Profile(profileName, sshHostAddress, sshHostPort, sshProxyPort, sshUsername, sshPassword, sshPrivateKey);
        assertNotNull("Profile is null", profile);

        assertEquals("Profile Name is incorrect", profileName, profile.getProfileName());
        assertEquals("SSH Host Address is incorrect", sshHostAddress, profile.getSshHostAddress());
        assertEquals("SSH Host Port is incorrect", sshHostPort, profile.getSshHostPort());
        assertEquals("SSH Proxy Port is incorrect", sshProxyPort, profile.getSshProxyPort());
        assertEquals("SSH Username is incorrect", sshUsername, profile.getSshUsername());
        assertEquals("SSH Password is incorrect", sshPassword, profile.getSshPassword());
        assertEquals("SSH Private Key is incorrect", sshPrivateKey, profile.getSshPrivateKey());
    }
}