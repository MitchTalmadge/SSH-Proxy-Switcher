package net.liveforcode.SSHProxySwitcher.GUI;

import junit.framework.TestCase;
import net.liveforcode.SSHProxySwitcher.Profile;

public class MainInterfaceTest extends TestCase {

    public void testRefreshProfileList() throws Exception {
        Profile[] profiles = new Profile[] {new Profile("Test Profile", "test.com", 22, 2000, "root", "password", null)};

        MainInterface mainInterface = new MainInterface(null);
        mainInterface.refreshProfileList(profiles);

        //TODO: Check if profiles were added correctly.
    }
}