package org.noos.xing.yasaf.plaf.view.listener;

import org.noos.xing.yasaf.view.ViewContext;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContextPutItemListener implements ItemListener {
    private ViewContext viewContext;
    private Object key;

    public ContextPutItemListener(ViewContext viewContext, Object key) {
        this.viewContext = viewContext;
        this.key = key;
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED)
            viewContext.put(key, e.getItem());
    }
    
}
