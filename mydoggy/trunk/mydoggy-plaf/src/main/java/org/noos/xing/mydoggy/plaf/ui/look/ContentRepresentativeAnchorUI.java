package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

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

        SwingUtil.registerDragGesture(c, new RepresentativeAnchorUIDragGesture(descriptor));
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

    protected class RepresentativeAnchorUIDragGesture extends DragGestureAdapter {
        private ToolWindowAnchor lastAnchor;

        public RepresentativeAnchorUIDragGesture(DockableDescriptor descriptor) {
            super(descriptor);
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            // Acquire locks
            acquireLocks();

            // Start Drag
            dge.startDrag(Cursor.getDefaultCursor(),
                          new MyDoggyTransferable(manager,
                                                  MyDoggyTransferable.BAR2BAR_DF,
                                                  dockable.getId()),
                          this);

            // Fire startDrag Event
            descriptor.getToolBar().propertyChange(new PropertyChangeEvent(label, "startDrag", null, dge));

            // Setup ghostImage
            if (resourceManager.getBoolean("drag.icon.useDefault", false)) {
                setGhostImage(dge.getDragOrigin(),
                              resourceManager.getBufferedImage(MyDoggyKeySpace.DRAG));
            } else {
                JComponent representativeAnchor = descriptor.getRepresentativeAnchor();
                BufferedImage ghostImage = new BufferedImage(representativeAnchor.getWidth(),
                                                             representativeAnchor.getHeight(),
                                                             BufferedImage.TYPE_INT_RGB);
                representativeAnchor.print(ghostImage.createGraphics());
                setGhostImage(dge.getDragOrigin(), ghostImage);
            }

            lastAnchor = null;
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!checkStatus())
                return;

            // Obtain anchor for location
            ToolWindowAnchor newAnchor = manager.getToolWindowAnchor(
                    SwingUtil.convertPointFromScreen(dsde.getLocation(), manager)
            );

            // Produce updatedGhostImage
            if (newAnchor != lastAnchor) {
                if (!resourceManager.getBoolean("drag.icon.useDefault", false)) {
                    resetGhostImage();

                    if (newAnchor == null) {
                        updatedGhostImage = ghostImage;
                        manager.getBar(lastAnchor).setTempShowed(false);
                    } else {
                        if (manager.getBar(newAnchor).getAvailableTools() == 0)
                            manager.getBar(newAnchor).setTempShowed(true);

                        switch (newAnchor) {
                            case LEFT:
                                switch (descriptor.getAnchor()) {
                                    case LEFT:
                                        updatedGhostImage = ghostImage;
                                        break;
                                    case RIGHT:
                                        updatedGhostImage = GraphicsUtil.rotate(ghostImage, Math.PI);
                                        break;
                                    default:
                                        updatedGhostImage = GraphicsUtil.rotate(ghostImage, 1.5 * Math.PI);
                                        break;
                                }
                                break;
                            case RIGHT:
                                switch (descriptor.getAnchor()) {
                                    case LEFT:
                                        updatedGhostImage = GraphicsUtil.rotate(ghostImage, Math.PI);
                                        break;
                                    case RIGHT:
                                        updatedGhostImage = ghostImage;
                                        break;
                                    default:
                                        updatedGhostImage = GraphicsUtil.rotate(ghostImage, -1.5 * Math.PI);
                                        break;
                                }
                                break;
                            case TOP:
                            case BOTTOM:
                                switch (descriptor.getAnchor()) {
                                    case LEFT:
                                        updatedGhostImage = GraphicsUtil.rotate(ghostImage, -1.5 * Math.PI);
                                        break;
                                    case RIGHT:
                                        updatedGhostImage = GraphicsUtil.rotate(ghostImage, 1.5 * Math.PI);
                                        break;
                                    default:
                                        updatedGhostImage = ghostImage;
                                        break;
                                }
                                break;
                        }
                    }
                } else
                    updatedGhostImage = ghostImage;

                lastAnchor = newAnchor;
            }

            updateGhostImage(dsde.getLocation(), updatedGhostImage);
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocksOne();

            // Restore graphics
            if (lastAnchor != null)
                manager.getBar(lastAnchor).setTempShowed(false);
            else {
                manager.getBar(LEFT).setTempShowed(false);
                manager.getBar(RIGHT).setTempShowed(false);
                manager.getBar(TOP).setTempShowed(false);
                manager.getBar(BOTTOM).setTempShowed(false);
            }

            // Fire endDrag event
            descriptor.getToolBar().propertyChange(new PropertyChangeEvent(label, "endDrag", null, dsde));

            // cleanup glassPane
            cleanupGhostImage();
            lastAnchor = null;

            releaseLocksTwo();
        }

    }


}