package org.noos.xing.mydoggy.plaf.ui;

import javax.swing.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GlassPanel extends JPanel implements ContainerListener {
    private GlassPaneMouseAdapter adapter;

    public GlassPanel(RootPaneContainer rootPaneContainer) {
        adapter = new GlassPaneMouseAdapter(rootPaneContainer);
        setOpaque(false);
        setVisible(false);
        setLayout(null);
        addContainerListener(this);
    }

    public void componentAdded(ContainerEvent e) {
        setVisible(true);
        if (getComponentCount() == 1) {
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
            addMouseWheelListener(adapter);
        }
    }

    public void componentRemoved(ContainerEvent e) {
        if (getComponentCount() == 0) {
            setVisible(false);
            removeMouseListener(adapter);
            removeMouseMotionListener(adapter);
            removeMouseWheelListener(adapter);
        }
    }

    public void setVisible(boolean aFlag) {
        if (!aFlag) {
            if (getComponentCount() == 0)
                super.setVisible(aFlag);
        } else
            super.setVisible(aFlag);
    }

}
