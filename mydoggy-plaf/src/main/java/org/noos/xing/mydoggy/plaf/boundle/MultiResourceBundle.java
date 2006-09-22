package org.noos.xing.mydoggy.plaf.boundle;

import org.noos.xing.mydoggy.plaf.collections.SetEnumeration;

import java.util.*;

/**
 * @author Angelo De Caro
 */
public class MultiResourceBundle extends ResourceBundle {
    private Map<String, Object> lookup;
    private Properties unresolved;
    private boolean trackUnresolved;

    public MultiResourceBundle() {
        lookup = new HashMap<String, Object>();
        unresolved = new Properties();
    }

    public Object handleGetObject(String key) {
        if (key == null)
            throw new NullPointerException("Cannot use null for retrieve object from resource bundle.");

        Object result = lookup.get(key);
        if (result == null && trackUnresolved)
            putUnresolvedKey(key);

        return result != null ? result : key;
    }

    public Enumeration<String> getKeys() {
        ResourceBundle parent = this.parent;
        return new SetEnumeration<String>(lookup.keySet(), parent != null ? parent.getKeys() : null);
    }

    public void addResource(String baseName, Locale locale, ClassLoader classLoader) {
        ResourceBundle bundle = classLoader != null ? ResourceBundle.getBundle(baseName, locale, classLoader)
                                : ResourceBundle.getBundle(baseName, locale);
        for (Enumeration<String> e = bundle.getKeys(); e.hasMoreElements();) {
            String key = e.nextElement();
            lookup.put(key, bundle.getObject(key));
        }
    }

    public Object putObject(String key, Object value) {
        return lookup.put(key, value);
    }

    public boolean containsKey(String key) {
        return key != null && lookup.get(key) != null;
    }

    public void setTrackUnresolved(boolean track) {
        this.trackUnresolved = track;
    }

    public Properties getUnresolved() {
        return unresolved;
    }

    protected void putUnresolvedKey(String key) {
        unresolved.put(key, key);
    }

}
