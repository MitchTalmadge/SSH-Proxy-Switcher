package net.liveforcode.SSHProxySwitcher.Utilities;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FileUtilitiesTest {

    @Test
    public void testGetRootDirectory() throws Exception {
        File rootDirectory = FileUtilities.getRootDirectory();
        assertNotNull("Root Directory is null", rootDirectory);
    }
}