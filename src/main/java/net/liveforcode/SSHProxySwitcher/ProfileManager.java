package net.liveforcode.SSHProxySwitcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfileManager {

    final File xmlFile;
    public ArrayList<ProfileListener> profileListenerList = new ArrayList<>();
    public ArrayList<Profile> loadedProfiles = new ArrayList<>();

    public ProfileManager(File xmlFile) {
        this.xmlFile = xmlFile;

        createProfilesXml(xmlFile);
        try {
            Profile[] profiles = loadProfilesFromXML(new FileInputStream(xmlFile));
            if (profiles != null) {
                loadedProfiles.addAll(Arrays.asList(profiles));
                onProfilesReloaded(loadedProfiles);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public static Profile[] loadProfilesFromXML(InputStream xmlFileInputStream) {
        try {
            Document parsedDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFileInputStream);
            if (parsedDocument.getDocumentElement().getTagName().equals("Profiles")) {
                NodeList profileList = parsedDocument.getDocumentElement().getElementsByTagName("Profile");
                Profile[] profiles = new Profile[profileList.getLength()];
                for (int i = 0; i < profileList.getLength(); i++) {
                    Profile profile = new Profile();
                    Element profileElement = (Element) profileList.item(i);

                    profile.setProfileName(profileElement.getAttribute("name"));

                    Element sshSettingsElement = (Element) profileElement.getElementsByTagName("SSHSettings").item(0);

                    String sshHostAddress = sshSettingsElement.getElementsByTagName("HostAddress").item(0).getTextContent();
                    profile.setSshHostAddress(sshHostAddress);

                    int sshHostPort = Integer.parseInt(sshSettingsElement.getElementsByTagName("HostPort").item(0).getTextContent());
                    profile.setSshHostPort(sshHostPort);

                    int sshProxyPort = Integer.parseInt(sshSettingsElement.getElementsByTagName("ProxyPort").item(0).getTextContent());
                    profile.setSshProxyPort(sshProxyPort);

                    String sshUsername = sshSettingsElement.getElementsByTagName("Username").item(0).getTextContent();
                    profile.setSshUsername(sshUsername);

                    String sshPassword = sshSettingsElement.getElementsByTagName("Password").item(0).getTextContent();
                    profile.setSshPassword(sshPassword); //TODO: Encrypt/Decrypt Password

                    File sshPrivateKey = new File(sshSettingsElement.getElementsByTagName("PrivateKey").item(0).getTextContent());
                    profile.setSshPrivateKey(sshPrivateKey);

                    profiles[i] = profile;
                }
                return profiles;
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createProfilesXml(File profilesFile) {
        if (profilesFile.exists())
            return;

        try {
            Document newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element documentElement = newDocument.createElement("Profiles");
            newDocument.appendChild(documentElement);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            Result output = new StreamResult(profilesFile);
            Source input = new DOMSource(newDocument);
            transformer.transform(input, output);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public File getXmlFile() {
        return xmlFile;
    }

    public interface ProfileListener {

        void onProfilesReloaded(ArrayList<Profile> loadedProfiles);

        void onProfileUpdated(Profile profile);

        void onProfileAdded(Profile profile);

        void onProfileRemoved(Profile profile);
    }
}
