package org.noos.xing.mydoggy.plaf.cleaner;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface CleanerAggregator extends Cleaner {

    void addCleaner(Cleaner cleaner);

    void removeCleaner(Cleaner cleaner);

    void addBefore(Cleaner beforeCleaner, Cleaner cleaner);

    void addAfter(Cleaner beforeCleaner, Cleaner cleaner);
}
