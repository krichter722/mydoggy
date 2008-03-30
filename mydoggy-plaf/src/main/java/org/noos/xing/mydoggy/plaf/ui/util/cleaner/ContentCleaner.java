package org.noos.xing.mydoggy.plaf.ui.util.cleaner;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ContentManagerListener;
import org.noos.xing.mydoggy.event.ContentManagerEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentCleaner implements CleanerAggregator, ContentManagerListener {
    protected ContentManager manager;
    protected Content content;
    protected List<Cleaner> cleaners;


    public ContentCleaner(ContentManager manager, Content content) {
        this.manager = manager;
        this.content = content;
        manager.addContentManagerListener(this);
    }


    public void cleanup() {
        if (cleaners != null)
            for (Cleaner cleaner : cleaners) {
                cleaner.cleanup();
            }

        manager.removeContentManagerListener(this);
    }

    public void addCleaner(Cleaner cleaner) {
        if (cleaners == null)
            cleaners = new ArrayList<Cleaner>();
        cleaners.add(cleaner);
    }

    public void contentAdded(ContentManagerEvent event) {
    }

    public void contentRemoved(ContentManagerEvent event) {
        if (event.getContent() == content)
            cleanup();
    }

    public void contentSelected(ContentManagerEvent event) {
    }
}