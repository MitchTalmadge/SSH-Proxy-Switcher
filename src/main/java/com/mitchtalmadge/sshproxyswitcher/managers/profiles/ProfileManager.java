package com.mitchtalmadge.sshproxyswitcher.managers.profiles;

import com.mitchtalmadge.sshproxyswitcher.utilities.FileUtilities;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ProfileManager {

    protected static final File profilesFile = new File(FileUtilities.getRootDirectory() + "/profiles.ser");
    protected ArrayList<Profile> loadedProfiles;

    protected ArrayList<LoadedProfilesListener> listeners = new ArrayList<>();

    public void saveProfiles() {
        if (loadedProfiles != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(profilesFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

                int profilesCount = loadedProfiles.size(); //Used to specify how many profiles are saved, and how many should be loaded.
                objectOutputStream.writeInt(profilesCount);

                for (Profile profile : loadedProfiles) {
                    objectOutputStream.writeObject(profile);
                }
                objectOutputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (LoadedProfilesListener listener : listeners)
                listener.loadedProfilesUpdated(loadedProfiles);
        }
    }

    public void loadProfiles() {
        loadedProfiles = new ArrayList<>();

        try {
            FileInputStream fileInputStream = new FileInputStream(profilesFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            int profilesCount = objectInputStream.readInt();

            for (int i = 0; i < profilesCount; i++) {
                Profile loadedProfile = (Profile) objectInputStream.readObject();
                loadedProfiles.add(loadedProfile);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (LoadedProfilesListener listener : listeners)
            listener.loadedProfilesUpdated(loadedProfiles);
    }

    public ArrayList<Profile> getLoadedProfiles() {
        return loadedProfiles;
    }

    public void addProfile(Profile profileToAdd) {
        if (loadedProfiles == null)
            loadProfiles();

        loadedProfiles.add(profileToAdd);

        saveProfiles();
    }

    public void deleteProfile(Profile profileToDelete) {
        if (loadedProfiles == null)
            loadProfiles();

        Iterator<Profile> profileIterator = loadedProfiles.iterator();
        while (profileIterator.hasNext()) {
            Profile profile = profileIterator.next();
            if (profile.getProfileName().equals(profileToDelete.getProfileName()))
                profileIterator.remove();
        }

        saveProfiles();
    }

    public void addLoadedProfilesListener(LoadedProfilesListener listener) {
        listeners.add(listener);
    }

    public interface LoadedProfilesListener {
        void loadedProfilesUpdated(ArrayList<Profile> loadedProfiles);
    }
}