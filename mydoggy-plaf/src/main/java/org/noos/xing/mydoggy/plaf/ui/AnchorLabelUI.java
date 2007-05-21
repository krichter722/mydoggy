package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragAndDropLock;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowTrasferable;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.translucent.TranslucentPanel;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

/**
 * @author Angelo De Caro
 */
public class AnchorLabelUI extends MetalLabelUI {
    static final Color start = new Color(255, 212, 151);
    static final Color end = new Color(255, 244, 204);
    static final Color gray = new Color(247, 243, 239);

    private static ResourceBundle resourceBundle = ResourceBoundles.getResourceBundle();

    private JComponent label;

    protected LineBorder labelBorder;

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected DockedTypeDescriptor dockedTypeDescriptor;

    private AnchorLabelMouseAdapter adapter;

    private Timer flashingTimer;
    private int flasingDuration;
    private boolean flashingState;

    private TranslucentPanel previewPanel;

    public AnchorLabelUI(ToolWindowDescriptor descriptor, ToolWindow toolWindow) {
        this.descriptor = descriptor;
        this.toolWindow = toolWindow;
        
        this.dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        this.dockedTypeDescriptor.addPropertyChangeListener(this);
    }

    public void installUI(JComponent c) {
        super.installUI(c);

        this.label = c;
        labelBorder = new LineBorder(Color.GRAY, 1, true, 3, 3);
        c.setBorder(labelBorder);

        DragGesture dragGesture = new DragGesture();
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_MOVE, dragGesture);
        dragSource.addDragSourceMotionListener(dragGesture);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        toolWindow.removePropertyChangeListener(this);
        c.removeMouseListener(adapter);
        c.removeMouseMotionListener(adapter);
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

        adapter = new AnchorLabelMouseAdapter();
        c.addMouseListener(adapter);
        c.addMouseMotionListener(adapter);

        descriptor.getToolWindow().addInternalPropertyChangeListener(this);
    }

    public void update(Graphics g, JComponent c) {
        Rectangle bounds = c.getBounds();
        if (toolWindow.isFlashing()) {

            if (flashingState) {
                GraphicsUtil.fillRect(g, new Rectangle(0, 0, bounds.width, bounds.height),
                                      start, end, null, GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
            } else {
                g.setColor(gray);
                g.fillRect(0, 0, bounds.width, bounds.height);
            }

            if (flashingTimer == null) {
                flashingTimer = new Timer(500, new ActionListener() {
                    long start = 0;

                    public void actionPerformed(ActionEvent e) {
                        if (start == 0)
                            start = System.currentTimeMillis();
                        flashingState = !flashingState;
                        SwingUtil.repaint(label);
                        if (flasingDuration != -1 && System.currentTimeMillis() - start > flasingDuration)
                            toolWindow.setFlashing(false);
                    }
                });
            }
            if (!flashingTimer.isRunning()) {
                flashingTimer.start();
            }
        } else {
            if (flashingTimer != null) {
                flashingTimer.stop();
                flashingTimer = null;
            }

            if (c.isOpaque()) {
                GraphicsUtil.fillRect(g, new Rectangle(0, 0, bounds.width, bounds.height),
                                      start, end, null, GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
            } else {
                g.setColor(gray);
                g.fillRect(0, 0, bounds.width, bounds.height);
            }
        }
        paint(g, c);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();

        if ("visible".equals(propertyName)) {
            boolean visible = (Boolean) e.getNewValue();
            label.setOpaque(visible);
            if (visible) {
                labelBorder.setLineColor(Color.BLACK);

                descriptor.getToolBar().ensureVisible(label);
            } else
                labelBorder.setLineColor(Color.GRAY);

            toolWindow.setFlashing(false);
            SwingUtil.repaint(label);
        } else if ("UI".equals(propertyName)) {
            adapter.propertyChange(e);
        } else if ("flash".equals(propertyName)) {
            if (!toolWindow.isVisible()) {
                flasingDuration = -1;
                SwingUtil.repaint(label);
            }
        } else if ("flash.duration".equals(propertyName)) {
            if (!toolWindow.isVisible()) {
                flasingDuration = (Integer) e.getNewValue();
                SwingUtil.repaint(label);
            }
        } else if ("previewDelay".equals(propertyName)) {
            adapter.setPreviewDelay((Integer)e.getNewValue());
        }
    }



    class AnchorLabelMouseAdapter extends MouseInputAdapter implements ActionListener, PropertyChangeListener {

        JPopupMenu popupMenu;

        JMenuItem visible;
        JMenuItem aggregate;
        JCheckBoxMenuItem floatingMode;
        JCheckBoxMenuItem dockedMode;
        JCheckBoxMenuItem pinnedMode;

        JMenu moveTo;
        JMenuItem right;
        JMenuItem left;
        JMenuItem top;
        JMenuItem bottom;

        Timer previewTimer;
        boolean firstPreview = true;


        public AnchorLabelMouseAdapter() {
            initPopupMenu();
            previewTimer = new Timer(dockedTypeDescriptor.getPreviewDelay(), this);
            descriptor.getToolWindow().addInternalPropertyChangeListener(this);
        }

        public void mouseClicked(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            previewTimer.stop();
            actionPerformed(new ActionEvent(previewTimer, 0, "stop"));

            if (SwingUtilities.isLeftMouseButton(e)) {
                int onmask = MouseEvent.SHIFT_DOWN_MASK;
                if ((e.getModifiersEx() & onmask) == onmask) {
                    if (toolWindow.isVisible()) {
                        toolWindow.setVisible(false);
                    } else {
                        toolWindow.aggregate();
                        toolWindow.setActive(true);
                    }
                } else {
                    if (toolWindow.isVisible()) {
                        toolWindow.setVisible(false);
                    } else {
                        toolWindow.setActive(true);
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled()) {
                    enableVisible();
                    enableMoveToItem();
                    enableUserDefined();

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
//            if (label.getBorder() != labelBorder)
            label.setBorder(labelBorder);
            labelBorder.setLineColor(Color.BLACK);
            SwingUtil.repaint(label);
        }

        public void mouseEntered(MouseEvent e) {
            if (!toolWindow.isVisible()) {
                if (previewPanel == null)
                    previewTimer.start();
            }

            if (toolWindow.isFlashing())
                return;

            Component source = e.getComponent();
            if (!source.isOpaque()) {
                labelBorder.setLineColor(Color.BLACK);
                SwingUtil.repaint(source);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (e.getX() == label.getWidth() || e.getX() < 0 ||
                e.getY() == label.getHeight() || e.getY() < 0)
                firstPreview = false;

            previewTimer.stop();
            actionPerformed(new ActionEvent(previewTimer, 0, "stop"));

            if (toolWindow.isFlashing())
                return;

            Component source = e.getComponent();
            if (!source.isOpaque()) {
                labelBorder.setLineColor(Color.GRAY);
                SwingUtil.repaint(source);
            }
        }

        public void mouseDragged(MouseEvent e) {
            firstPreview = false;
            previewTimer.stop();
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == previewTimer) {
                if ("stop".equals(e.getActionCommand())) {
                    if (previewPanel != null && !firstPreview) {
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(label);

                        if (frame != null) {
                            GlassPanel glassPane = (GlassPanel) frame.getRootPane().getGlassPane();
                            glassPane.remove(previewPanel);
                            SwingUtil.repaint(glassPane);
                            glassPane.setVisible(false);

                            SwingUtil.repaint(frame);

                            previewPanel = null;
                        }
                    }
                    firstPreview = false;
                } else if (dockedTypeDescriptor.isPreviewEnabled()) {
                    Container contentContainer = ((DockedContainer) descriptor.getToolWindowContainer()).getContentContainer();
                    int width = 176;
                    int height = 132;

                    // Show Preview
                    RootPaneContainer rootPaneContainer = (RootPaneContainer) SwingUtilities.getWindowAncestor(label);
                    if (rootPaneContainer != null) {
                        JMenuBar jMenuBar = rootPaneContainer instanceof JFrame ?
                                             ((JFrame)rootPaneContainer).getJMenuBar() : null;

                        firstPreview = true;
                        previewTimer.stop();

                        GlassPanel glassPane = (GlassPanel) rootPaneContainer.getGlassPane();

                        if (previewPanel != null)
                            glassPane.remove(previewPanel);

                        previewPanel = new TranslucentPanel(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));
                        previewPanel.setAlpha(dockedTypeDescriptor.getPreviewTransparentRatio());
                        previewPanel.setSize(width + 4, height + 4);

                        Container mainContainer = descriptor.getManager();
                        switch (descriptor.getToolWindow().getAnchor()) {
                            case LEFT:
                                previewPanel.setLocation(
                                        mainContainer.getX() +
                                        label.getX() + label.getWidth() + 3,

                                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                        mainContainer.getY() +
                                        label.getY() +
                                        (descriptor.getToolBar(TOP).getAvailableTools() != 0 ? 23 : 0)
                                );
                                break;
                            case TOP:
                                previewPanel.setLocation(
                                        mainContainer.getX() +
                                        label.getX() +
                                        (descriptor.getToolBar(LEFT).getAvailableTools() != 0 ? 23 : 0),

                                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                        mainContainer.getY() +
                                        label.getY() + label.getHeight() + 3
                                );
                                break;
                            case BOTTOM:
                                previewPanel.setLocation(
                                        mainContainer.getX() +
                                        label.getX() +
                                        (descriptor.getToolBar(LEFT).getAvailableTools() != 0 ? 23 : 0),

                                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                        mainContainer.getY() +
                                        mainContainer.getHeight() -
                                        (descriptor.getToolBar(TOP).getAvailableTools() != 0 ? 0 : 23) -
                                        previewPanel.getHeight() - 26
                                );
                                break;
                            case RIGHT:
                                previewPanel.setLocation(
                                        mainContainer.getX() +
                                        mainContainer.getWidth() -
                                        (descriptor.getToolBar(LEFT).getAvailableTools() != 0 ? 0 : 23) -
                                        previewPanel.getWidth() - 26,

                                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                        mainContainer.getY() +
                                        label.getY() +
                                        (descriptor.getToolBar(TOP).getAvailableTools() != 0 ? 23 : 0) 
                                );
                                break;
                        }

                        if (previewPanel.getY() + previewPanel.getHeight() >
                                mainContainer.getY() + mainContainer.getHeight() - 26) {

                            previewPanel.setLocation(
                                    previewPanel.getX(),

                                    (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                    mainContainer.getY() +
                                    mainContainer.getHeight() -
                                    (descriptor.getToolBar(BOTTOM).getAvailableTools() != 0 ? 23 : 0) -
                                    previewPanel.getHeight() - 3
                            );
                        }

                        if (previewPanel.getX() + previewPanel.getWidth() >
                                mainContainer.getX() + mainContainer.getWidth() - 26) {

                            previewPanel.setLocation(
                                    mainContainer.getX() +
                                    mainContainer.getWidth() -
                                    (descriptor.getToolBar(RIGHT).getAvailableTools() != 0 ? 23 : 0) -
                                    previewPanel.getWidth() - 3,
                                    
                                    previewPanel.getY()
                            );
                        }


                        previewPanel.add(contentContainer, "1,1,FULL,FULL");

                        glassPane.add(previewPanel);
                        glassPane.setVisible(true);
                        SwingUtil.repaint(glassPane);
                    }
                }
            }


            String actionCommand = e.getActionCommand();
            if ("visible".equals(actionCommand)) {
                if (toolWindow.isActive()) {
                    toolWindow.setActive(false);
                    toolWindow.setVisible(false);
                } else if (toolWindow.isVisible())
                    toolWindow.setVisible(false);
                else
                    toolWindow.setActive(true);
            } else if ("aggregate".equals(actionCommand)) {
                if (toolWindow.isActive()) {
                    toolWindow.setActive(false);
                    toolWindow.setVisible(false);
                } else if (toolWindow.isVisible())
                    toolWindow.setVisible(false);
                else {
                    toolWindow.aggregate();
                    toolWindow.setActive(true);
                }
            } else if ("move.right".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.RIGHT);
            } else if ("move.left".equals(actionCommand)) {
                toolWindow.setAnchor(LEFT);
            } else if ("move.top".equals(actionCommand)) {
                toolWindow.setAnchor(TOP);
            } else if ("move.bottom".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.BOTTOM);
            } else if ("floating".equals(actionCommand)) {
                if (floatingMode.isSelected()) {
                    toolWindow.setType((descriptor.isFloatingWindow()) ? ToolWindowType.FLOATING_FREE : ToolWindowType.FLOATING);
                    dockedMode.setVisible(!floatingMode.isSelected());
                } else
                    toolWindow.setType(ToolWindowType.DOCKED);
            } else if ("docked".equals(actionCommand)) {
                toolWindow.setType(dockedMode.isSelected() ? ToolWindowType.DOCKED : ToolWindowType.SLIDING);
            } else if ("pinned".equals(actionCommand)) {
                toolWindow.setAutoHide(!toolWindow.isAutoHide());
            }
//            if (toolWindow.isActive()) {
//                SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        toolWindow.setActive(true);
//                    }
//                });
//            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if ("autoHide".equals(evt.getPropertyName())) {
                pinnedMode.setState(!(Boolean) evt.getNewValue());
            } else if ("type".equals(evt.getPropertyName())) {
                ToolWindowType type = (ToolWindowType) evt.getNewValue();
                dockedMode.setState(type == ToolWindowType.DOCKED);
                dockedMode.setVisible(type != ToolWindowType.FLOATING);
                pinnedMode.setVisible(type != ToolWindowType.SLIDING);

                floatingMode.setState(type == ToolWindowType.FLOATING);
            } else if ("UI".equals(evt.getPropertyName())) {
                SwingUtilities.updateComponentTreeUI(popupMenu);

                DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
                SwingUtilities.updateComponentTreeUI(descriptor.getToolsMenu());
            }
        }


        protected void initPopupMenu() {
            popupMenu = new JPopupMenu("ToolWindowBarPopupMenu");
            popupMenu.setLightWeightPopupEnabled(false);

            // Visible
            visible = new JMenuItem();
            visible.setActionCommand("visible");
            visible.addActionListener(this);

            aggregate = new JMenuItem();
            aggregate.setText(resourceBundle.getString("@@tool.aggregate"));
            aggregate.setActionCommand("aggregate");
            aggregate.addActionListener(this);

            floatingMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING);
            floatingMode.setText(resourceBundle.getString("@@tool.mode.floating"));
            floatingMode.setActionCommand("floating");
            floatingMode.addActionListener(this);

            dockedMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.DOCKED);
            dockedMode.setText(resourceBundle.getString("@@tool.mode.docked"));
            dockedMode.setActionCommand("docked");
            dockedMode.addActionListener(this);

            pinnedMode = new JCheckBoxMenuItem(null, !toolWindow.isAutoHide());
            pinnedMode.setText(resourceBundle.getString("@@tool.mode.pinned"));
            pinnedMode.setActionCommand("pinned");
            pinnedMode.addActionListener(this);

            // MoveTo SubMenu
            moveTo = new JMenu();
            moveTo.getPopupMenu().setLightWeightPopupEnabled(false);
            moveTo.setText(resourceBundle.getString("@@tool.moveTo"));

            right = new JMenuItem();
            right.setText(resourceBundle.getString("@@tool.move.right"));
            right.setActionCommand("move.right");
            right.addActionListener(this);

            left = new JMenuItem();
            left.setText(resourceBundle.getString("@@tool.move.left"));
            left.setActionCommand("move.left");
            left.addActionListener(this);

            top = new JMenuItem();
            top.setText(resourceBundle.getString("@@tool.move.top"));
            top.setActionCommand("move.top");
            top.addActionListener(this);

            bottom = new JMenuItem();
            bottom.setText(resourceBundle.getString("@@tool.move.bottom"));
            bottom.setActionCommand("move.bottom");
            bottom.addActionListener(this);

            moveTo.add(right);
            moveTo.add(left);
            moveTo.add(top);
            moveTo.add(bottom);

            popupMenu.add(pinnedMode);
            popupMenu.add(dockedMode);
            popupMenu.add(floatingMode);
            popupMenu.add(moveTo);
            popupMenu.addSeparator();
            popupMenu.add(visible);
            popupMenu.add(aggregate);
        }

        protected void enableVisible() {
            aggregate.setVisible(!toolWindow.isVisible());
            visible.setText(toolWindow.isVisible() ?
                            resourceBundle.getString("@@tool.hide") :
                            resourceBundle.getString("@@tool.show"));

            if (toolWindow.getType() == ToolWindowType.DOCKED) {
                dockedMode.setVisible(((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled());
                floatingMode.setVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
            } else if (toolWindow.getType() == ToolWindowType.SLIDING)
                floatingMode.setVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
        }

        protected void enableMoveToItem() {
            ToolWindowAnchor anchor = toolWindow.getAnchor();
            if (anchor == LEFT) {
                left.setVisible(false);
                right.setVisible(true);
                top.setVisible(true);
                bottom.setVisible(true);
            } else if (anchor == ToolWindowAnchor.RIGHT) {
                left.setVisible(true);
                right.setVisible(false);
                top.setVisible(true);
                bottom.setVisible(true);
            } else if (anchor == ToolWindowAnchor.BOTTOM) {
                left.setVisible(true);
                right.setVisible(true);
                top.setVisible(true);
                bottom.setVisible(false);
            } else if (anchor == TOP) {
                left.setVisible(true);
                right.setVisible(true);
                top.setVisible(false);
                bottom.setVisible(true);
            }
        }

        private JMenu old;

        protected void enableUserDefined() {
            DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
            if (old != null) {
                popupMenu.remove(old);
            }

            JMenu menu = descriptor.getToolsMenu();
            if (menu.getMenuComponentCount() > 0) {
                popupMenu.add(menu, 4);
                old = menu;
            }
        }

        public void setPreviewDelay(int previewDelay) {
            previewTimer.setDelay(previewDelay);
        }
        
    }

    class DragGesture implements DragGestureListener, DragSourceMotionListener, DragSourceListener {
        private BufferedImage ghostImage;
        private ToolWindowAnchor lastAnchor;

        public void dragGestureRecognized(DragGestureEvent dge) {
            if (DragAndDropLock.isLocked()) {
                DragAndDropLock.setDragAndDropStarted(false);
                return;
            }
            DragAndDropLock.setLocked(true);
            DragAndDropLock.setDragAndDropStarted(true);

            // Start Drag
            dge.startDrag(Cursor.getDefaultCursor(), new ToolWindowTrasferable(toolWindow), this);

            // Prepare glassPane for ghost image
            GlassPanel glassPane = (GlassPanel) SwingUtilities.getRootPane(descriptor.getManager()).getGlassPane();

            // TODO: è la cosa migliore??
            if (previewPanel != null)
                glassPane.remove(previewPanel);

            glassPane.setVisible(true);

            Point p = (Point) dge.getDragOrigin().clone();
            SwingUtilities.convertPointToScreen(p, descriptor.getManager());
            SwingUtilities.convertPointFromScreen(p, glassPane);

            // Build orginalDragImage            
            JComponent c = descriptor.getAnchorLabel();
            ghostImage = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
            descriptor.getAnchorLabel().print(ghostImage.createGraphics());

            descriptor.getToolBar().propertyChange(new PropertyChangeEvent(label, "startDrag", null, dge));

            // Setup glasspane
            glassPane.setPoint(p);
            glassPane.setDraggingImage(ghostImage);
            glassPane.repaint();

            lastAnchor = null;
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!DragAndDropLock.isDragAndDropStarted() || ghostImage == null)
                return;

            GlassPanel glassPane = (GlassPanel) SwingUtilities.getRootPane(descriptor.getManager()).getGlassPane();

            Point p = (Point) dsde.getLocation().clone();
            SwingUtilities.convertPointFromScreen(p, glassPane);
            glassPane.setPoint(p);

            p = (Point) dsde.getLocation().clone();
            SwingUtilities.convertPointFromScreen(p, descriptor.getManager());
            ToolWindowAnchor newAnchor = descriptor.getToolWindowAnchor(p);

            if (newAnchor != lastAnchor) {
                Rectangle dirtyRegion = glassPane.getRepaintRect();

                if (newAnchor == null) {
                    glassPane.setDraggingImage(ghostImage);
                    descriptor.getToolBar(lastAnchor).setTempShowed(false);
                } else {
                    if (descriptor.getToolBar(newAnchor).getAvailableTools() == 0)
                        descriptor.getToolBar(newAnchor).setTempShowed(true);

                    switch (newAnchor) {
                        case LEFT:
                            switch (descriptor.getToolWindow().getAnchor()) {
                                case LEFT:
                                    glassPane.setDraggingImage(ghostImage);
                                    break;
                                case RIGHT:
                                    glassPane.setDraggingImage(GraphicsUtil.rotate(ghostImage, Math.PI));
                                    break;
                                default:
                                    glassPane.setDraggingImage(GraphicsUtil.rotate(ghostImage, 1.5 * Math.PI));
                                    break;
                            }
                            break;
                        case RIGHT:
                            switch (descriptor.getToolWindow().getAnchor()) {
                                case LEFT:
                                    glassPane.setDraggingImage(GraphicsUtil.rotate(ghostImage, Math.PI));
                                    break;
                                case RIGHT:
                                    glassPane.setDraggingImage(ghostImage);
                                    break;
                                default:
                                    glassPane.setDraggingImage(GraphicsUtil.rotate(ghostImage, -1.5 * Math.PI));
                                    break;
                            }
                            break;
                        case TOP:
                        case BOTTOM:
                            switch (descriptor.getToolWindow().getAnchor()) {
                                case LEFT:
                                    glassPane.setDraggingImage(GraphicsUtil.rotate(ghostImage, -1.5 * Math.PI));
                                    break;
                                case RIGHT:
                                    glassPane.setDraggingImage(GraphicsUtil.rotate(ghostImage, 1.5 * Math.PI));
                                    break;
                                default:
                                    glassPane.setDraggingImage(ghostImage);
                                    break;
                            }
                            break;
                    }
                }

                lastAnchor = newAnchor;
                glassPane.repaint(dirtyRegion);
            }

            glassPane.repaint(glassPane.getRepaintRect());
        }

        public void dragEnter(DragSourceDragEvent dsde) {
        }

        public void dragOver(DragSourceDragEvent dsde) {
        }

        public void dropActionChanged(DragSourceDragEvent dsde) {
        }

        public void dragExit(DragSourceEvent dse) {
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!DragAndDropLock.isDragAndDropStarted() || ghostImage == null)
                return;

            DragAndDropLock.setDragAndDropStarted(false);

            if (lastAnchor != null)
                descriptor.getToolBar(lastAnchor).setTempShowed(false);

            descriptor.getToolBar().propertyChange(new PropertyChangeEvent(label, "endDrag", null, dsde));

            GlassPanel glassPane = (GlassPanel) SwingUtilities.getRootPane(descriptor.getManager()).getGlassPane();

            Point p = (Point) dsde.getLocation().clone();
            SwingUtilities.convertPointFromScreen(p, glassPane);

            glassPane.setDraggingImage(null);
            glassPane.setVisible(false);

            ghostImage = null;
            lastAnchor = null;

            DragAndDropLock.setLocked(false);

            SwingUtilities.getWindowAncestor(descriptor.getManager()).repaint();

        }

    }

}
