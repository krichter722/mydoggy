package org.noos.xing.mydoggy.plaf.ui.util.cleaner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultCleanerAggregator implements CleanerAggregator{
    protected List<Cleaner> cleaners;

    public void cleanup() {
        if (cleaners != null)
            for (Cleaner cleaner : cleaners) {
                cleaner.cleanup();
            }
    }

    public void addCleaner(Cleaner cleaner) {
        if (cleaners == null)
            cleaners = new ArrayList<Cleaner>();
        cleaners.add(cleaner);
    }

}