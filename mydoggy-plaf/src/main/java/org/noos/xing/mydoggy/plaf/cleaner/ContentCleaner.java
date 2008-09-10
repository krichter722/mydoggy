package org.noos.xing.mydoggy.plaf.cleaner;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ContentManagerListener;
import org.noos.xing.mydoggy.event.ContentManagerEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentCleaner extends DefaultCleanerAggregator implements ContentManagerListener {
    protected ContentManager manager;
    protected Content content;


    public ContentCleaner(ContentManager manager, Content content) {
        this.manager = manager;
        this.content = content;
        manager.addContentManagerListener(this);
    }


    public void cleanup() {
        super.cleanup();
        
        manager.removeContentManagerListener(this);
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