package org.noos.xing.mydoggy.plaf.ui.util;

import java.util.ResourceBundle;
import java.util.Enumeration;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DummyResourceBundle extends ResourceBundle {

    protected Object handleGetObject(String key) {
        return key;
    }

    public Enumeration<String> getKeys() {
        return new Enumeration<String>() {
            public boolean hasMoreElements() {
                return false;
            }

            public String nextElement() {
                return null;
            }
        };
    }
    
}
