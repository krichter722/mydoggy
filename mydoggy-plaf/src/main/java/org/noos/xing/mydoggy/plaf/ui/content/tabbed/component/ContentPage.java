package org.noos.xing.mydoggy.plaf.ui.content.tabbed.component;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.TabbedContentUI;
import org.noos.xing.mydoggy.plaf.ui.ResourceBundleManager;
import org.noos.xing.mydoggy.plaf.ui.icons.AggregateIcon;
import org.noos.xing.mydoggy.plaf.ui.icons.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentPage implements TabbedContentUI {
    static Icon closeImgI;
    static Icon maxImgI;
    static Icon closeImgD;
    static Icon maxImgD;

    static {
        maxImgI = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/detach.png");
        closeImgI = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/close.png");

        maxImgD = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/detachInactive.png");
        closeImgD = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/closeInactive.png");
    }

    private JTabbedContentManager tabbedPane;

    private Content content;

    private AccessibleContext accessible;

    private String title;
    private Icon icon;
    private JPopupMenu popupMenu;
    private JPopupMenu stdPopupMenu;

    private boolean closable;
    private boolean detachable;
    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;

    private Icon contentIcon;

    private MaximizeAction maximizeAction;

    public ContentPage(Content content, JTabbedContentManager tabbedPane, final AccessibleContext accessible) {
        this.content = content;
        this.tabbedPane = tabbedPane;
        this.accessible = accessible;

        this.closable = tabbedPane.isCloseable();
        this.detachable = tabbedPane.isDetachable();
        this.transparentMode = true;
        this.transparentRatio = 0.8f;
        this.transparentDelay = 1000;
    }

    public ContentPage(JTabbedContentManager tabbedPane, final AccessibleContext accessible) {
        this(null, tabbedPane, accessible);
    }


    public Content getContent() {
        return content;
    }

    public boolean isCloseable() {
        return closable;
    }

    public void setCloseable(boolean closable) {
        this.closable = closable;
        SwingUtil.repaint(tabbedPane);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
        SwingUtil.repaint(tabbedPane);
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        this.transparentMode = transparentMode;
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        this.transparentRatio = transparentRatio;
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setTransparentDelay(int transparentDelay) {
        this.transparentDelay = transparentDelay;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    public void setTitle(String title) {
        this.title = title;
        contentIcon = null;
    }

    public String getTitle() {
        return title;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
        contentIcon = null;
    }

    public AccessibleContext getAccessible() {
        return accessible;
    }

    public void setAccessible(AccessibleContext accessible) {
        this.accessible = accessible;
    }

    public String getToolTipTextAt(MouseEvent e, int index, String defaultTip) {
        if (index != -1) {
            AggregateIcon compositeIcon = (AggregateIcon) ((AggregateIcon) getContentIcon()).getRightIcon();

            Point point = SwingUtilities.convertPoint(tabbedPane, e.getPoint(), getDestination());

            if (isDetachable()) {
                Rectangle detachIconRect = compositeIcon.getLastPaintedLeftRec();
                if (point.getX() > detachIconRect.x && point.getX() < detachIconRect.x + detachIconRect.width) {
                    return ResourceBundleManager.getInstance().getString("@@tab.content.detach");
                }
            }

            if (isCloseable()) {
                Rectangle closeIconRect = compositeIcon.getLastPaintedRightRec();
                if (point.getX() > closeIconRect.x && point.getX() < closeIconRect.x + closeIconRect.width) {
                    return ResourceBundleManager.getInstance().getString("@@tab.content.close");
                }
            }
            return (defaultTip.equals("")) ? null : defaultTip;
        }
        return (defaultTip.equals("")) ? null : defaultTip;
    }

    public Icon getContentIcon() {
        if (contentIcon == null) {
            String title = getTitle();
            Icon icon = getIcon();

            // Left Part
            Icon titleIcon = null;
            if (title != null)
                titleIcon = new DynamicTextIcon();

            // Right Part
            contentIcon = new AggregateIcon(new AggregateIcon(icon, titleIcon, SwingConstants.HORIZONTAL),
                                               new CloseMaximizeIcon(),
                                               SwingConstants.HORIZONTAL);
        }
        return contentIcon;
    }

    public boolean isDetachFired(Point point) {
        Point relativeMousePoint = SwingUtilities.convertPoint(tabbedPane, point, getDestination());
        AggregateIcon uiCompositeIcon = getUICompositeIcon();

        Rectangle detachIconRect = uiCompositeIcon.getLastPaintedLeftRec();
        return (isDetachable() && ((relativeMousePoint.getX() > detachIconRect.x && relativeMousePoint.getX() < detachIconRect.x + detachIconRect.width) ||
                                   (point.getX() > detachIconRect.x && point.getX() < detachIconRect.x + detachIconRect.width)));
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public boolean isCloseFired(Point point) {
        Point relativeMousePoint = SwingUtilities.convertPoint(tabbedPane, point, getDestination());
        AggregateIcon uiCompositeIcon = getUICompositeIcon();

        Rectangle closeIconRect = uiCompositeIcon.getLastPaintedRightRec();
        return (isCloseable() && ((relativeMousePoint.getX() > closeIconRect.x && relativeMousePoint.getX() < closeIconRect.x + closeIconRect.width) ||
                                  (point.getX() > closeIconRect.x && point.getX() < closeIconRect.x + closeIconRect.width)));
    }


    private Component getDestination() {
        for (int i = 0, size = tabbedPane.getComponentCount(); i < size; i++) {
            if (tabbedPane.getComponent(i) instanceof JViewport)
                return ((JViewport) tabbedPane.getComponent(i)).getView();
        }
        return tabbedPane;
    }

    private AggregateIcon getUICompositeIcon() {
        return (AggregateIcon) ((AggregateIcon) getContentIcon()).getRightIcon();
    }

    public void showPopupMenu(Component source, final MouseEvent mouseEvent, final int mouseOverTab, JPopupMenu defaultContentPopupMenu) {
        JPopupMenu popupMenu = getPopupMenu();
        if (popupMenu == null)
            popupMenu = defaultContentPopupMenu;

        if (popupMenu == null) {
            if (stdPopupMenu == null) {
                // Init stdPopupMenu
                stdPopupMenu = new JPopupMenu("Content Page Popup");
                stdPopupMenu.add(new JMenuItem(new AbstractAction(ResourceBundleManager.getInstance().getString("@@tabbed.page.close")) {
                    public void actionPerformed(ActionEvent e) {
                        tabbedPane.fireCloseTabEvent(mouseEvent, mouseOverTab);
                    }
                }));
                stdPopupMenu.add(new JMenuItem(new AbstractAction(ResourceBundleManager.getInstance().getString("@@tabbed.page.closeAll")) {
                    public void actionPerformed(ActionEvent e) {
                        for (int i = 0, size = tabbedPane.getTabCount(); i < size; i++)
                            tabbedPane.fireCloseTabEvent(mouseEvent, i);
                    }
                }));
                stdPopupMenu.add(new JMenuItem(new AbstractAction(ResourceBundleManager.getInstance().getString("@@tabbed.page.closeAllButThis")) {
                    public void actionPerformed(ActionEvent e) {
                        for (int i = 0, size = tabbedPane.getTabCount(); i < size; i++)
                            if (i != mouseOverTab)
                                tabbedPane.fireCloseTabEvent(mouseEvent, i);
                    }
                }));
                stdPopupMenu.addSeparator();
                stdPopupMenu.add(new JMenuItem(new AbstractAction(ResourceBundleManager.getInstance().getString("@@tabbed.page.detach")) {
                    public void actionPerformed(ActionEvent e) {
                        tabbedPane.fireDetachTabEvent(mouseEvent, mouseOverTab);
                    }
                }));
                stdPopupMenu.add(maximizeAction = new MaximizeAction());
            } else {
                maximizeAction.putValue(Action.NAME, tabbedPane.isMaximized() ?
                                                     ResourceBundleManager.getInstance().getString("@@tabbed.page.restore") :
                                                     ResourceBundleManager.getInstance().getString("@@tabbed.page.maximize")
                );
            }

            popupMenu = stdPopupMenu;
        }

        if (popupMenu != null)
            popupMenu.show(source, mouseEvent.getX(), mouseEvent.getY());
    }


    class DynamicTextIcon extends TextIcon {

        public DynamicTextIcon() {
            super(tabbedPane, title, TextIcon.ROTATE_NONE);
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Component tabComponent = (Component) accessible.getAccessibleChild(0);

            boolean isSelected = false;
            int index = -1;
            for (int i = 0, size = tabbedPane.getTabCount(); i < size; i++) {
                if (tabbedPane.getComponentAt(i) == tabComponent) {
                    index = i;
                    if (tabbedPane.getSelectedIndex() == i)
                        isSelected = true;
                    break;
                }
            }

            if (index != -1) {
                if (isSelected) {
                    this.setForeground(tabbedPane.getForegroundAt(index));
                } else
                    this.setForeground(tabbedPane.getForegroundAt(index).brighter().brighter());
                super.paintIcon(c, g, x, y);
            } else
                throw new IllegalStateException("Invalid Content Index");
        }
    }

    class CloseMaximizeIcon extends AggregateIcon {
        private boolean isSelected;

        public CloseMaximizeIcon() {
            super(maxImgD, closeImgD, SwingConstants.HORIZONTAL);
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Component tabComponent = (Component) accessible.getAccessibleChild(0);
            isSelected = false;
            for (int i = 0, size = tabbedPane.getTabCount(); i < size; i++) {
                if (tabbedPane.getComponentAt(i) == tabComponent) {
                    if (tabbedPane.getSelectedIndex() == i)
                        isSelected = true;
                    break;
                }
            }
            super.paintIcon(c, g, x, y);
        }

        public Icon getLeftIcon() {
            return isSelected ? maxImgI : maxImgD;
        }

        public Icon getRightIcon() {
            return isSelected ? closeImgI : closeImgD;
        }

        public boolean isLeftVisible() {
            return isDetachable();
        }

        public boolean isRightVisible() {
            return isCloseable();
        }

    }

    class MaximizeAction extends AbstractAction {
        public MaximizeAction() {
            super(ResourceBundleManager.getInstance().getString("@@tabbed.page.maximize"));
        }

        public void actionPerformed(ActionEvent e) {
            tabbedPane.setMaximized(!tabbedPane.isMaximized());
        }
    }
}
