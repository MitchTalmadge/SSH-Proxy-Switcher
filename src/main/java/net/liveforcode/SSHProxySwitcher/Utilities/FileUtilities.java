package net.liveforcode.SSHProxySwitcher.Utilities;

import java.io.File;
import java.net.URISyntaxException;

public class FileUtilities {

    public static File getRootDirectory() {
        try {
            return new File(FileUtilities.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getAbsoluteFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

}
