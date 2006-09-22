package org.noos.xing.mydoggy.plaf.boundle;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author Angelo De Caro
 */
public class DefaultResourceBoundle implements ResourceBoundle {
    private String name;
    private MultiResourceBundle resourceBundle;
    private Hashtable<String, MessageFormat> formats;

    public DefaultResourceBoundle() {
        formats = new Hashtable<String, MessageFormat>();
        resourceBundle = new MultiResourceBundle();
    }

    public DefaultResourceBoundle(String name) {
        this();
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public String getString(String key) {
        if (key == null)
            return null;
        try {
            return resourceBundle.getString(key);
        } catch (Exception except) {
            return key;
        }
    }

    public String[] getStrings(String... keys) {
        if (keys == null)
            return null;

        for (int i = 0; i < keys.length; i++) {
            keys[i] = getString(keys[i]);
        }

        return keys;
    }

    public String format(String key, Object... args) {
        if (key == null)
            return null;

        MessageFormat mf;
        String msg;

        try {
            mf = formats.get(key);
            if (mf == null) {
                try {
                    msg = resourceBundle.getString(key);
                } catch (MissingResourceException except) {
                    return key;
                }
                mf = new MessageFormat(msg);
                formats.put(key, mf);
            }
            return mf.format(args);
        } catch (Exception except) {
            return "An internal error occured while processing getString " + key;
        }
    }

    public void addResource(String baseName, Locale locale, ClassLoader classLoader) {
        if (resourceBundle == null)
            resourceBundle = new MultiResourceBundle();
        resourceBundle.addResource(baseName, locale, classLoader);
    }

    public boolean containsKey(String key) {
        return resourceBundle.containsKey(key);
    }

    public void list(PrintWriter out) {
        out.println("-- listing properties -- " + System.currentTimeMillis());

        for (Enumeration e = resourceBundle.getKeys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String val = (String) resourceBundle.getObject(key);
            out.println(key + '=' + val);
        }
    }

    public Object putString(String key, String value) {
        if (key != null && value != null)
            return resourceBundle.putObject(key, value);
        else
            return null;
    }

    public String[] getStringArray(String key) {
        try {
            return resourceBundle.getStringArray(key);
        } catch (MissingResourceException except) {
            return new String[]{except.getMessage()};
        }
    }

    public Object getObject(String key) {
        try {
            return resourceBundle.getObject(key);
        } catch (MissingResourceException except) {
            return except.getMessage();
        }
    }

    public void setTrackUnresolved(boolean trackUnresolved) {
        resourceBundle.setTrackUnresolved(trackUnresolved);
    }

    public Properties getUnresolved() {
        return resourceBundle.getUnresolved();
    }


}
