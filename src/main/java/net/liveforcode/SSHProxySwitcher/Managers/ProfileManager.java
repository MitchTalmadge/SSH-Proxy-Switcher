package net.liveforcode.SSHProxySwitcher.Managers;

import net.liveforcode.SSHProxySwitcher.Profile;
import net.liveforcode.SSHProxySwitcher.Utilities.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfileManager {

    private ArrayList<Profile> loadedProfiles;

    public void loadProfilesFromXmlFile(File xmlFile) {
        if (!xmlFile.exists())
            createXmlFile(xmlFile);
        if (!isXmlFileValid(xmlFile))
            createXmlFile(xmlFile);
        try {
            Document parsedDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
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
                this.loadedProfiles = new ArrayList<>();
                loadedProfiles.addAll(Arrays.asList(profiles));
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private boolean isXmlFileValid(File xmlFile) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            documentBuilder.setErrorHandler(new ErrorHandler() { //Prevent from printing to System.err
                @Override
                public void warning(SAXParseException e) throws SAXException {
                    ;
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    throw e;
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    throw e;
                }
            });
            Document document = documentBuilder.parse(xmlFile);
            return document.getDocumentElement().getTagName().equals("Profiles");
        } catch (SAXException | IOException | ParserConfigurationException e) {
            return false;
        }
    }

    private void createXmlFile(File xmlFile) {
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
}
