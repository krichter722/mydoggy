package org.noos.xing.mydoggy.plaf.ui.util.cleaner;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface CleanerAggregator extends Cleaner {

    void addCleaner(Cleaner cleaner);

    void addBefore(Cleaner beforeCleaner, Cleaner cleaner);

}
