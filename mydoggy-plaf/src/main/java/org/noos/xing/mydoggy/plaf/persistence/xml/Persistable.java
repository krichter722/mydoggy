package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.xml.sax.SAXException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface Persistable {

    void save(XMLWriter writer) throws SAXException;
    
}
