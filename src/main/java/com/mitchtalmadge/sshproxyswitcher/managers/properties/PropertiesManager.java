package com.mitchtalmadge.sshproxyswitcher.managers.properties;

import com.mitchtalmadge.sshproxyswitcher.SSHProxySwitcher;

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
                    SSHProxySwitcher.reportError(Thread.currentThread(), e);
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        SSHProxySwitcher.reportError(Thread.currentThread(), e);
                    }
                }
            } catch (FileNotFoundException e) {
                SSHProxySwitcher.reportError(Thread.currentThread(), e);
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
                    SSHProxySwitcher.reportError(Thread.currentThread(), e);
                } finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        SSHProxySwitcher.reportError(Thread.currentThread(), e);
                    }
                }
            } catch (FileNotFoundException e) {
                SSHProxySwitcher.reportError(Thread.currentThread(), e);
            }
        }
    }

    public String getPropertyAsString(PropertiesEnum property) {
        if (properties == null)
            return property.getDefaultValue();

        String propertyString = properties.getProperty(property.getKey());
        if (propertyString == null)
            return property.getDefaultValue();
        return propertyString;
    }

    public boolean getPropertyAsBool(PropertiesEnum property) {
        String propertyString = getPropertyAsString(property);
        return Boolean.parseBoolean(propertyString);
    }

    public int getPropertyAsInt(PropertiesEnum property) {
        String propertyString = getPropertyAsString(property);
        try {
            return Integer.parseInt(propertyString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setProperty(PropertiesEnum property, String value)
    {
        if(properties == null)
            return;
        properties.setProperty(property.getKey(), value == null ? property.getDefaultValue() : value);
        saveProperties();
    }
}
