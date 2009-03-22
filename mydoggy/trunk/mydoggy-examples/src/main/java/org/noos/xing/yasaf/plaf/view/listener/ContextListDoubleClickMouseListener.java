package org.noos.xing.yasaf.plaf.view.listener;

import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContextListDoubleClickMouseListener implements MouseListener {
    private ViewContext viewContext;
    private Object key;

    public ContextListDoubleClickMouseListener(ViewContext viewContext, Object key) {
        this.viewContext = viewContext;
        this.key = key;
    }

    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)
            viewContext.put(key, e);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }


}