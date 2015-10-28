package net.liveforcode.SSHProxySwitcher.Utilities;

import org.w3c.dom.Element;

public class XMLUtilities {

    public static Element getFirstElementByName(String name, Element parent)
    {
            return (Element) parent.getElementsByTagName(name).item(0);
    }

    public static int getElementAsInt(Element element)
    {
        return Integer.parseInt(element.getTextContent());
    }

}
