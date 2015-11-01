package net.liveforcode.SSHProxySwitcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class SSHProxySwitcherTest {

    private SSHProxySwitcher sshProxySwitcher;

    @Before
    public void setUp() throws Exception {
        this.sshProxySwitcher = new SSHProxySwitcher();
        assertNotNull("SSHProxySwitcher is null", sshProxySwitcher);
    }

    @Test
    public void testSSHProxySwitcherInitializesProperly() throws Exception {
        sshProxySwitcher.init();

        assertNotNull("ProfileManager is null", sshProxySwitcher.getProfileManager());
        assertNotNull("SSHManager is null", sshProxySwitcher.getSSHManager());
    }

}