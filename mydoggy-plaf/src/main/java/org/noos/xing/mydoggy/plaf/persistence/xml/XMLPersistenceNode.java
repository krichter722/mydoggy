package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.PersistenceDelegateCallback;
import org.w3c.dom.Element;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XMLPersistenceNode implements PersistenceDelegateCallback.PersistenceNode {
    protected Element element;


    public XMLPersistenceNode() {
    }

    public XMLPersistenceNode(Element element) {
        this.element = element;
    }


    public String getName() {
        return element.getNodeName();
    }

    public boolean containsAttribute(String name) {
        String attr = element.getAttribute(name);

        return attr != null && !"".equals(attr.trim());
    }

    public String getAttributeValue(String name) {
        return element.getAttribute(name);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        try {
            String attr = element.getAttribute(name);
            if (attr != null && !"".equals(attr.trim()))
                return Boolean.parseBoolean(attr);
            else
                return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getInteger(String name, int defaultValue) {
        try {
            String attr = element.getAttribute(name);
            if (attr != null && !"".equals(attr.trim()))
                return Integer.parseInt(attr);
            else
                return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float getFloat(String name, float defaultValue) {
        try {
            String attr = element.getAttribute(name);
            if (attr != null && !"".equals(attr.trim()))
                return Float.parseFloat(attr);
            else
                return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }


    public Element getElement() {
        return element;
    }

    public XMLPersistenceNode setElement(Element element) {
        this.element = element;

        return this;
    }
}
