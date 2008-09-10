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
            for (Cleaner cleaner : cleaners.toArray(new Cleaner[cleaners.size()])) {
                cleaner.cleanup();
            }
            cleaners.clear();
        }
    }

    public void addCleaner(Cleaner cleaner) {
        if (cleaners == null)
            cleaners = new ArrayList<Cleaner>();

        if (cleaners.contains(cleaner))
            return;

        cleaners.add(cleaner);
    }

    public void removeCleaner(Cleaner cleaner) {
        cleaners.remove(cleaner);
    }

    public void addBefore(Cleaner beforeCleaner, Cleaner cleaner) {
        if (cleaners.contains(cleaner))
            return;

        if (cleaners == null)
            addCleaner(cleaner);

        int index = cleaners.indexOf(beforeCleaner);

        if (index == -1)
            cleaners.add(cleaner);
        else
            cleaners.add(index, cleaner);
    }

    public void addAfter(Cleaner afterCleaner, Cleaner cleaner) {
        if (cleaners == null)
            addCleaner(cleaner);

        int index = cleaners.indexOf(afterCleaner);

        if (index == -1)
            cleaners.add(cleaner);
        else if  (index + 1 >= cleaners.size())
            cleaners.add(cleaner);
        else
            cleaners.add(index + 1, cleaner);
    }

}