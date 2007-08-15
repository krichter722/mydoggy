package org.noos.xing.mydoggy.plaf.persistence;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedContentManager {
    private List<PersistedContent> contents;

    public PersistedContentManager() {
        this.contents = new ArrayList<PersistedContent>();
    }

    public List<PersistedContent> getContents() {
        return contents;
    }
    
}