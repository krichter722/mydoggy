package org.noos.xing.mydoggy.plaf.cleaner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultCleanerAggregator implements CleanerAggregator {
    protected List<Cleaner> cleaners;

    public void cleanup() {
        if (cleaners != null) {
            for (Cleaner cleaner : cleaners) {
                cleaner.cleanup();
            }
            cleaners.clear();
        }
    }

    public void addCleaner(Cleaner cleaner) {
        if (cleaners == null)
            cleaners = new ArrayList<Cleaner>();
        cleaners.add(cleaner);
    }

    public void addBefore(Cleaner beforeCleaner, Cleaner cleaner) {
        if (cleaners == null)
            addCleaner(cleaner);
        int index = cleaners.indexOf(beforeCleaner);
        if (index == -1)
            cleaners.add(cleaner);
        else
            cleaners.add(index, cleaner);
    }

}