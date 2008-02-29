package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.plaf.PlafObservable;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.1
 */
public interface PlafContent extends Content, PlafObservable {

    /**
     * TODO: remove this...
     * Notify selection.
     *
     * @param selected <tt>true</tt> is the content must be selected, <tt>false</tt> otherwise.
     * @since 1.3.1
     */
    void fireSelected(boolean selected);
    
}
