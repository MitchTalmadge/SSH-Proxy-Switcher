package com.mitchtalmadge.sshproxyswitcher.managers.properties;

import java.io.*;
import java.util.Properties;

public class PropertiesManager {

    private Properties properties;
    private File propertiesFile;

    public void loadPropertiesFromFile(File file) throws PropertiesException {
        if (file == null)
            throw new PropertiesException("Properties File is null");
        this.propertiesFile = file;
        Properties properties = new Properties();
        this.properties = properties;
        if (propertiesFile.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(propertiesFile);
                try {
                    properties.load(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        validateProperties();
    }

    private void validateProperties() {
        boolean shouldSave = false;
        for (PropertiesEnum enom : PropertiesEnum.values()) {
            String property = properties.getProperty(enom.getKey());
            if (property == null || property.isEmpty()) {
                properties.setProperty(enom.getKey(), enom.getDefaultValue());
                shouldSave = true;
            }
        }
        if (shouldSave)
            saveProperties();
    }

    public void saveProperties() {
        if (properties != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(propertiesFile);
                try {
                    properties.store(outputStream, null);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
