package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ContentWindow extends MultiSplitWindow<Content> {

    void setVisible(boolean visible);

    boolean isDisposed();

    void dispose();
    
}
