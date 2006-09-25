package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowTransferHandler;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class AnchorLabelUI extends MetalLabelUI {

    static final Color start = new Color(255, 212, 151);
    static final Color end = new Color(255, 244, 204);

    protected JLabel label;
    protected LineBorder labelBorder;

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;

    public AnchorLabelUI(ToolWindowDescriptor descriptor, ToolWindow toolWindow) {
        this.descriptor = descriptor;
        this.toolWindow = toolWindow;
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        label = (JLabel) c;
        labelBorder = new LineBorder(Color.GRAY, 1, true, 3, 3);
        label.setBorder(labelBorder);

        label.setTransferHandler(new ToolWindowTransferHandler(toolWindow));
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

        AnchorLabelMouseAdapter adapter = new AnchorLabelMouseAdapter();
        c.addMouseListener(adapter);
        c.addMouseMotionListener(adapter);
        descriptor.getToolWindow().addInternalPropertyChangeListener(this);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        toolWindow.removePropertyChangeListener(this);
    }

    public void update(Graphics g, JComponent c) {
        if (c.isOpaque())
            GraphicsUtil.fillRect(g, new Rectangle(0, 0, c.getWidth(), c.getHeight()),
                                  start, end, null, GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
        else {
            g.setColor(c.getParent().getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
        paint(g, c);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if ("visible".equals(e.getPropertyName())) {
            boolean visible = (Boolean) e.getNewValue();
            label.setOpaque(visible);
            if (visible)
                labelBorder.setLineColor(Color.BLACK);
            else
                labelBorder.setLineColor(Color.GRAY);

            SwingUtil.repaint(label);

            // Move to Visible  TODO: fail with FLOATING_WINDOW and for other resaons.
            JViewport viewport = (JViewport) label.getParent().getParent();
            System.out.println(label.getBounds());
            viewport.scrollRectToVisible(label.getBounds());
        }
    }


    class AnchorLabelMouseAdapter extends MouseInputAdapter implements ActionListener, PropertyChangeListener {

        JPopupMenu popupMenu;

        JMenuItem visible;
        JCheckBoxMenuItem floatingMode;
        JCheckBoxMenuItem dockedMode;
        JCheckBoxMenuItem pinnedMode;

        JMenu moveTo;
        JMenuItem right;
        JMenuItem left;
        JMenuItem top;
        JMenuItem bottom;

        public AnchorLabelMouseAdapter() {
            initPopupMenu();
            descriptor.getToolWindow().addInternalPropertyChangeListener(this);
        }

        public void mouseClicked(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (toolWindow.isVisible()) {
                    toolWindow.setVisible(false);
                } else {
                    toolWindow.setActive(true);
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled()) {
                    enableVisible();
                    enableMoveToItem();
                    enableUserDefined();

                    popupMenu.show(label, e.getX(), e.getY());
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
            if (!label.isOpaque()) {
                labelBorder.setLineColor(Color.BLACK);
                SwingUtil.repaint(label);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (!label.isOpaque()) {
                labelBorder.setLineColor(Color.GRAY);
                SwingUtil.repaint(label);
            }
        }

        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if ("visible".equals(actionCommand)) {
                if (toolWindow.isActive()) {
                    toolWindow.setActive(false);
                    toolWindow.setVisible(false);
                } else if (toolWindow.isVisible())
                    toolWindow.setVisible(false);
                else
                    toolWindow.setActive(true);
            } else if ("move.right".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.RIGHT);
            } else if ("move.left".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.LEFT);
            } else if ("move.top".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.TOP);
            } else if ("move.bottom".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.BOTTOM);
            } else if ("floating".equals(actionCommand)) {
                if (floatingMode.isSelected()) {
                    toolWindow.setType((descriptor.isFloatingWindow()) ? ToolWindowType.FLOATING_WINDOW : ToolWindowType.FLOATING);
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
            }
        }


        public void mouseDragged(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                JComponent c = (JComponent) e.getSource();
                TransferHandler handler = c.getTransferHandler();
                handler.exportAsDrag(c, e, TransferHandler.MOVE);
            }
        }

        protected void initPopupMenu() {
            popupMenu = new JPopupMenu("ToolWindowBarPopupMenu");
            popupMenu.setLightWeightPopupEnabled(false);

            // Visible
            visible = new JMenuItem();
            visible.setActionCommand("visible");
            visible.addActionListener(this);

            floatingMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING);
            floatingMode.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.mode.floating"));
            floatingMode.setActionCommand("floating");
            floatingMode.addActionListener(this);

            dockedMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.DOCKED);
            dockedMode.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.mode.docked"));
            dockedMode.setActionCommand("docked");
            dockedMode.addActionListener(this);

            pinnedMode = new JCheckBoxMenuItem(null, !toolWindow.isAutoHide());
            pinnedMode.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.mode.pinned"));
            pinnedMode.setActionCommand("pinned");
            pinnedMode.addActionListener(this);

            // MoveTo SubMenu
            moveTo = new JMenu();
            moveTo.getPopupMenu().setLightWeightPopupEnabled(false);
            moveTo.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.moveTo"));

            right = new JMenuItem();
            right.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.move.right"));
            right.setActionCommand("move.right");
            right.addActionListener(this);

            left = new JMenuItem();
            left.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.move.left"));
            left.setActionCommand("move.left");
            left.addActionListener(this);

            top = new JMenuItem();
            top.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.move.top"));
            top.setActionCommand("move.top");
            top.addActionListener(this);

            bottom = new JMenuItem();
            bottom.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.move.bottom"));
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
        }

        protected void enableVisible() {
            visible.setText(toolWindow.isVisible() ?
                            ResourceBoundles.getResourceBoundle().getString("@@tool.hide") :
                            ResourceBoundles.getResourceBoundle().getString("@@tool.show"));
        }

        protected void enableMoveToItem() {
            ToolWindowAnchor anchor = toolWindow.getAnchor();
            if (anchor == ToolWindowAnchor.LEFT) {
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
            } else if (anchor == ToolWindowAnchor.TOP) {
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

            JMenu menu = descriptor.getUserDefinedMenu();
            if (menu.getComponentCount() > 0) {
                popupMenu.add(menu, 4);
                old = menu;
            }
        }

    }

}
