package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.RepresentativeAnchorDragGesture;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro
 */
public class ContentRepresentativeAnchorUI extends MetalLabelUI {
    protected JComponent label;

    protected LineBorder labelBorder;

    protected DockableDescriptor descriptor;
    protected Dockable dockable;
    protected ResourceManager resourceManager;

    protected RepresentativeAnchorMouseAdapter adapter;


    public ContentRepresentativeAnchorUI(DockableDescriptor descriptor) {
        this.descriptor = descriptor;
        this.dockable = descriptor.getDockable();
        this.resourceManager = descriptor.getResourceManager();
    }


    public void installUI(JComponent c) {
        super.installUI(c);

        this.label = c;
        labelBorder = new LineBorder(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_OUT_BORDER), 1, true, 3, 3);
        c.setBorder(labelBorder);
        c.setForeground(resourceManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND));

        SwingUtil.registerDragGesture(c, new RepresentativeAnchorDragGesture(descriptor, label));
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        dockable.removePropertyChangeListener(this);
        c.removeMouseListener(adapter);
        c.removeMouseMotionListener(adapter);
    }

    public void update(Graphics g, JComponent c) {
        c.setForeground(resourceManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND));

        updateAnchor(g, c,
                     resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START),
                     resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END),
                     false,
                     false);
        paint(g, c);
    }


    protected void installListeners(JLabel c) {
        super.installListeners(c);

        // Forse PropertyChangeListener
        String oldText = c.getText();
        if (oldText != null) {
            c.setText(null);
            c.setText(oldText);
        }

        oldText = c.getToolTipText();
        if (oldText != null) {
            c.setToolTipText(null);
            c.setToolTipText(oldText);
        }

        adapter = new RepresentativeAnchorMouseAdapter();
        c.addMouseListener(adapter);
        c.addMouseMotionListener(adapter);

// TODO       descriptor.getToolWindow().addPlafPropertyChangeListener(this);
    }

    protected void updateAnchor(Graphics g, JComponent c,
                                Color backgroundStart, Color backgroundEnd,
                                boolean active, boolean flashing) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;

        if (flashing || active) {
            GraphicsUtil.fillRect(g,
                                  r,
                                  backgroundStart,
                                  backgroundEnd,
                                  null,
                                  GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
        } else {
            g.setColor(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
            g.fillRect(0, 0, r.width, r.height);
        }
    }


    protected class RepresentativeAnchorMouseAdapter extends MouseInputAdapter {

        public RepresentativeAnchorMouseAdapter() {
        }

        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dockable.setMinimzed(false);
            } else if (SwingUtilities.isRightMouseButton(e)) {
            }

            label.setBorder(labelBorder);
            labelBorder.setLineColor(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_IN_BORDER));
            SwingUtil.repaint(label);
        }

        public void mouseEntered(MouseEvent e) {
            Component source = e.getComponent();

            labelBorder.setLineColor(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_IN_BORDER));
            SwingUtil.repaint(source);
        }

        public void mouseExited(MouseEvent e) {
            Component source = e.getComponent();

            labelBorder.setLineColor(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_OUT_BORDER));
            SwingUtil.repaint(source);
        }

        public void mouseDragged(MouseEvent e) {
        }

    }

}