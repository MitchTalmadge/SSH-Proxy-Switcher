package net.liveforcode.SSHProxySwitcher;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SSHProxySwitcherTest {

    private SSHProxySwitcher sshProxySwitcher;

    @Before
    public void setUp() throws Exception {
        sshProxySwitcher = new SSHProxySwitcher();
        assertNotNull("SSHProxySwitcher is null", sshProxySwitcher);
    }

    @Test
    public void testGetProfileManager() throws Exception {
        ProfileManager profileManager = sshProxySwitcher.getProfileManager();
        assertNotNull("ProfileManager is null", profileManager);
    }

    @Test
    public void testShowMainInterface() throws Exception {
        sshProxySwitcher.showMainInterface();
        assertTrue("GUI is not visible", sshProxySwitcher.mainInterface.isVisible());
    }
}