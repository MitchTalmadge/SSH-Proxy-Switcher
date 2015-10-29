package net.liveforcode.SSHProxySwitcher;

import net.liveforcode.SSHProxySwitcher.Utilities.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfileManager {

    final File xmlFile;
    final ArrayList<ProfileListener> profileListenerList;
    final ArrayList<Profile> loadedProfiles;

    public ProfileManager(File xmlFile) {
        this.xmlFile = xmlFile;
        profileListenerList = new ArrayList<>();
        loadedProfiles = new ArrayList<>();
    }

    public void addProfileListener(ProfileListener profileListener) {
        if (!this.profileListenerList.contains(profileListener))
            profileListenerList.add(profileListener);
    }

    public void removeProfileListener(ProfileListener profileListener) {
        profileListenerList.remove(profileListener);
    }

    private void onProfilesReloaded(ArrayList<Profile> loadedProfiles) {
        for (ProfileListener listener : profileListenerList)
            listener.onProfilesReloaded(loadedProfiles);
    }

    private void onProfileUpdated(Profile profile) {
        for (ProfileListener listener : profileListenerList)
            listener.onProfileUpdated(profile);
    }

    private void onProfileAdded(Profile profile) {
        for (ProfileListener listener : profileListenerList)
            listener.onProfileAdded(profile);
    }

    private void onProfileRemoved(Profile profile) {
        for (ProfileListener listener : profileListenerList)
            listener.onProfileRemoved(profile);
    }

    public void loadProfilesFromXML() {
        createProfilesXmlIfNotExists();

        try {
            Document parsedDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(xmlFile));
            if (parsedDocument.getDocumentElement().getTagName().equals("Profiles")) {
                NodeList profileList = parsedDocument.getDocumentElement().getElementsByTagName("Profile");
                Profile[] profiles = new Profile[profileList.getLength()];
                for (int i = 0; i < profileList.getLength(); i++) {
                    Profile profile = new Profile();
                    Element profileElement = (Element) profileList.item(i);

                    profile.setProfileName(profileElement.getAttribute("name"));

                    Element sshSettingsElement = XMLUtilities.getFirstElementByName("SSHSettings", profileElement);

                    String sshHostAddress = XMLUtilities.getFirstElementByName("HostAddress", sshSettingsElement).getTextContent();
                    profile.setSshHostAddress(sshHostAddress);

                    int sshHostPort = XMLUtilities.getElementAsInt(XMLUtilities.getFirstElementByName("HostPort", sshSettingsElement));
                    profile.setSshHostPort(sshHostPort);

                    int sshProxyPort = XMLUtilities.getElementAsInt(XMLUtilities.getFirstElementByName("ProxyPort", sshSettingsElement));
                    profile.setSshProxyPort(sshProxyPort);

                    String sshUsername = XMLUtilities.getFirstElementByName("Username", sshSettingsElement).getTextContent();
                    profile.setSshUsername(sshUsername);

                    String sshPassword = XMLUtilities.getFirstElementByName("Password", sshSettingsElement).getTextContent();
                    profile.setSshPassword(sshPassword); //TODO: Encrypt/Decrypt Password

                    File sshPrivateKey = new File(XMLUtilities.getFirstElementByName("PrivateKey", sshSettingsElement).getTextContent());
                    profile.setSshPrivateKey(sshPrivateKey);

                    profiles[i] = profile;
                }
                this.loadedProfiles.clear();
                this.loadedProfiles.addAll(Arrays.asList(profiles));
                onProfilesReloaded(new ArrayList<>(loadedProfiles));
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void createProfilesXmlIfNotExists() {
        if (xmlFile.exists())
            return;

        try {
            Document newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element documentElement = newDocument.createElement("Profiles");
            newDocument.appendChild(documentElement);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            Result output = new StreamResult(xmlFile);
            Source input = new DOMSource(newDocument);
            transformer.transform(input, output);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Profile> getLoadedProfiles() {
        return loadedProfiles;
    }

    public Profile[] getLoadedProfilesAsArray() {
        return loadedProfiles.toArray(new Profile[loadedProfiles.size()]);
    }

    public interface ProfileListener {

        void onProfilesReloaded(ArrayList<Profile> loadedProfiles);

        void onProfileUpdated(Profile profile);

        void onProfileAdded(Profile profile);

        void onProfileRemoved(Profile profile);
    }
}
