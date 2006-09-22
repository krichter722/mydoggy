package org.noos.xing.mydoggy.plaf.boundle;

/**
 * @author Angelo De Caro
 */
public interface ResourceBoundle {

    /**
     * Returns ResourceBounlde name.
     *
     * @return this ResourceBounlde name.
     */
    String getName();

    String getString(String key);

    String[] getStrings(String... keys);

    /**
     * Format the named getString using any number of arguments and return the
     * full getString text.
     *
     * @param key
     * @param args Argument list
     * @return The full getString text
     */
    String format(String key, Object... args);


}
