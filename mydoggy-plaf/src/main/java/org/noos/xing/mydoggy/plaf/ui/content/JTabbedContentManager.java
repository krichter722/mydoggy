package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.TransparencyAnimation;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.util.EventListener;

public class JTabbedContentManager extends JTabbedPane {

    private Frame parentFrame;
    private TabbedContentManagerUI paneUI;

    public JTabbedContentManager(Frame parentFrame) {
        super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.parentFrame = parentFrame;
        paneUI = new TabbedContentManagerUI();
        super.setUI(paneUI);
        setFocusCycleRoot(true);
    }

    public void setCloseEnabled(boolean b) {
        paneUI.setCloseEnabled(b);
    }

    public void setDetachEnabled(boolean b) {
        paneUI.setDetachEnabled(b);
    }

    public boolean isCloseEnabled() {
        return paneUI.isCloseEnabled();
    }

    public boolean isDetachEnabled() {
        return paneUI.isDetachEnabled();
    }

    public void setTabLayoutPolicy(int tabLayoutPolicy) {
    }

    public void setTabPlacement(int tabPlacement) {
    }

    public void setUI(TabbedPaneUI ui) {
    }

    public String getToolTipText(MouseEvent event) {
        int index = ((TabbedPaneUI) ui).tabForCoordinate(this, event.getX(), event.getY());
        if (index != -1)
            return paneUI.getToolTipTextAt(event, index, super.getToolTipText());

        return super.getToolTipText(event);
    }

    /**
     * TODO: ricordare la posizione dell'ultimo detach
     * @param index
     */
    public void detachTab(int index) {
        if (index < 0 || index >= getTabCount())
            return;

        final JDialog dialog = new JDialog(parentFrame, false);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Window parentWindow = SwingUtilities.windowForComponent(this);

        final int tabIndex = index;
        final JComponent c = (JComponent) getComponentAt(tabIndex);

        final Icon icon = getIconAt(tabIndex);
        final String title = getTitleAt(tabIndex);
        final String toolTip = getToolTipTextAt(tabIndex);
        final Border border = c.getBorder();

        removeTabAt(index);

        c.setPreferredSize(c.getSize());

        dialog.setTitle(title);
        dialog.getContentPane().add(c);
        Point location = parentWindow.getLocation();
        location.x += 5;
        location.y += 5;
        dialog.setLocation(location);
        dialog.pack();

        if (TransparencyManager.getInstance().isServiceAvailable()) {
            TransparencyListener transparencyListener = new TransparencyListener(dialog);
            dialog.addWindowListener(transparencyListener);
            dialog.addWindowFocusListener(transparencyListener);
        }

        dialog.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event) {
                dialog.dispose();

                insertTab(title, icon, c, toolTip, Math.min(tabIndex, getTabCount()));
                c.setBorder(border);
                setSelectedComponent(c);
            }

        });

        if (parentFrame == null) {
            WindowFocusListener windowFocusListener = new WindowFocusListener() {
                long start;
                long end;

                public void windowGainedFocus(WindowEvent e) {
                    start = System.currentTimeMillis();
                }

                public void windowLostFocus(WindowEvent e) {
                    end = System.currentTimeMillis();
                    long elapsed = end - start;
                    //System.out.println(elapsed);
                    if (elapsed < 100)
                        dialog.toFront();

                    dialog.removeWindowFocusListener(this);
                }
            };
            dialog.addWindowFocusListener(windowFocusListener);
        }

        dialog.toFront();
        dialog.setVisible(true);
    }

    /**
     * Adds a <code>CloseListener</code> to the tabbedPane.
     *
     * @param l the <code>CloseListener</code> to add
     * @see #fireCloseTabEvent
     * @see #removeCloseListener
     */
    public synchronized void addCloseListener(CloseListener l) {
        listenerList.add(CloseListener.class, l);
    }

    /**
     * Adds a <code>DetachListener</code> to the tabbedPane.
     *
     * @param l the <code>DetachListener</code> to add
     * @see #fireDetachTabEvent
     * @see #removeDetachListener
     */
    public synchronized void addDetachListener(DetachListener l) {
        listenerList.add(DetachListener.class, l);
    }

    /**
     * Adds a <code>DoubleClickListener</code> to the tabbedPane.
     *
     * @param l the <code>DoubleClickListener</code> to add
     * @see #fireDoubleClickTabEvent
     * @see #removeDoubleClickListener
     */
    public synchronized void addDoubleClickListener(DoubleClickListener l) {
        listenerList.add(DoubleClickListener.class, l);
    }

    /**
     * Adds a <code>PopupOutsideListener</code> to the tabbedPane.
     *
     * @param l the <code>PopupOutsideListener</code> to add
     * @see #firePopupOutsideTabEvent
     * @see #removePopupOutsideListener
     */
    public synchronized void addPopupOutsideListener(PopupOutsideListener l) {
        listenerList.add(PopupOutsideListener.class, l);
    }

    /**
     * Removes a <code>CloseListener</code> from this tabbedPane.
     *
     * @param l the <code>CloseListener</code> to remove
     * @see #fireCloseTabEvent
     * @see #addCloseListener
     */
    public synchronized void removeCloseListener(CloseListener l) {
        listenerList.remove(CloseListener.class, l);
    }

    /**
     * Removes a <code>DetachListener</code> from this tabbedPane.
     *
     * @param l the <code>DetachListener</code> to remove
     * @see #fireDetachTabEvent
     * @see #addDetachListener
     */
    public synchronized void removeDetachListener(DetachListener l) {
        listenerList.remove(DetachListener.class, l);
    }

    /**
     * Removes a <code>DoubleClickListener</code> from this tabbedPane.
     *
     * @param l the <code>DoubleClickListener</code> to remove
     * @see #fireDoubleClickTabEvent
     * @see #addDoubleClickListener
     */
    public synchronized void removeDoubleClickListener(DoubleClickListener l) {
        listenerList.remove(DoubleClickListener.class, l);
    }

    /**
     * Removes a <code>PopupOutsideListener</code> from this tabbedPane.
     *
     * @param l the <code>PopupOutsideListener</code> to remove
     * @see #firePopupOutsideTabEvent
     * @see #addPopupOutsideListener
     */
    public synchronized void removePopupOutsideListener(PopupOutsideListener l) {
        listenerList.remove(PopupOutsideListener.class, l);
    }

    /**
     * Sends a <code>MouseEvent</code>, whose source is this tabbedpane, to
     * every <code>CloseListener</code>. The method also updates the
     * <code>overTabIndex</code> of the tabbedPane with a value coming from
     * the UI. This method method is called each time a <code>MouseEvent</code>
     * is received from the UI when the user clicks on the close icon of the tab
     * which index is <code>overTabIndex</code>.
     *
     * @param e            the <code>MouseEvent</code> to be sent
     * @param overTabIndex the index of a tab, usually the tab over which the mouse is
     * @see #addCloseListener
     * @see EventListenerList
     */
    public void fireCloseTabEvent(MouseEvent e, int overTabIndex) {
        TabbedEvent event = new TabbedEvent(this, e, null, overTabIndex);
        for (CloseListener closeListener : getListeners(CloseListener.class)) {
            closeListener.closeOperation(event);
        }
    }

    /**
     * Sends a <code>MouseEvent</code>, whose source is this tabbedpane, to
     * every <code>DetachListener</code>. The method also updates the
     * <code>overTabIndex</code> of the tabbedPane with a value coming from
     * the UI. This method method is called each time a <code>MouseEvent</code>
     * is received from the UI when the user clicks on the max icon of the tab
     * which index is <code>overTabIndex</code>.
     *
     * @param e            the <code>MouseEvent</code> to be sent
     * @param overTabIndex the index of a tab, usually the tab over which the mouse is
     * @see #addDetachListener
     * @see EventListenerList
     */
    public void fireDetachTabEvent(MouseEvent e, int overTabIndex) {
        TabbedEvent event = new TabbedEvent(this, e, null, overTabIndex);

        EventListener maxListeners[] = getListeners(DetachListener.class);
        for (int i = 0; i < maxListeners.length; i++) {
            ((DetachListener) maxListeners[i]).detachOperation(event);
        }
    }

    /**
     * Sends a <code>MouseEvent</code>, whose source is this tabbedpane, to
     * every <code>DoubleClickListener</code>. The method also updates the
     * <code>overTabIndex</code> of the tabbedPane with a value coming from
     * the UI. This method method is called each time a <code>MouseEvent</code>
     * is received from the UI when the user double-clicks on the tab which
     * index is <code>overTabIndex</code>.
     *
     * @param e            the <code>MouseEvent</code> to be sent
     * @param overTabIndex the index of a tab, usually the tab over which the mouse is
     * @see #addDoubleClickListener
     * @see EventListenerList
     */
    public void fireDoubleClickTabEvent(MouseEvent e, int overTabIndex) {
        EventListener dClickListeners[] = getListeners(DoubleClickListener.class);
        for (int i = 0; i < dClickListeners.length; i++) {
            ((DoubleClickListener) dClickListeners[i]).doubleClickOperation(e);
        }
    }

    /**
     * Sends a <code>MouseEvent</code>, whose source is this tabbedpane, to
     * every <code>PopupOutsideListener</code>. The method also sets the
     * <code>overTabIndex</code> to -1. This method method is called each time
     * a <code>MouseEvent</code> is received from the UI when the user
     * right-clicks on the inactive part of a tabbedPane.
     *
     * @param e the <code>MouseEvent</code> to be sent
     * @see #addPopupOutsideListener
     * @see EventListenerList
     */
    public void firePopupOutsideTabEvent(MouseEvent e) {
        EventListener popupListeners[] = getListeners(PopupOutsideListener.class);
        for (int i = 0; i < popupListeners.length; i++) {
            ((PopupOutsideListener) popupListeners[i]).popupOutsideOperation(e);
        }
    }


    private class TransparencyListener extends WindowAdapter implements WindowFocusListener, ActionListener {
        private final TransparencyManager transparencyManager = TransparencyManager.getInstance();

        private TransparencyAnimation animation;

        private Timer timer;
        private Window window;

        public TransparencyListener(Window window) {
            this.window = window;
            this.animation = new TransparencyAnimation(window, 0.8f);
        }

        public void windowGainedFocus(WindowEvent e) {
            if (transparencyManager.isAlphaModeEnabled(e.getWindow())) {
                animation.hide();
                transparencyManager.setAlphaModeRatio(e.getWindow(), 0.0f);
            }
        }

        public void windowLostFocus(WindowEvent e) {
            if (!transparencyManager.isAlphaModeEnabled(e.getWindow())) {
                timer = new Timer(1000, this);
                timer.start();
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (timer.isRunning()) {
                timer.stop();
                synchronized (transparencyManager) {
                    animation.show();
                }
            }
        }

        public void windowClosing(WindowEvent event) {
            if (transparencyManager.isAlphaModeEnabled(event.getWindow())) {
                animation.hide();
                transparencyManager.setAlphaModeRatio(window, 0.0f);
            }
        }

    }
}

