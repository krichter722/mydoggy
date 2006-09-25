package org.noos.xing.mydoggy.plaf.ui.content;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventListener;

public class JTabbedContentManager extends JTabbedPane {
    private TabbedContentManagerUI paneUI;

    public JTabbedContentManager(Frame parentFrame) {
        super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        paneUI = new TabbedContentManagerUI(parentFrame);
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

    public void setPopupMenuAt(int index, JPopupMenu popupMenu) {
        checkIndex(index);
        paneUI.setPopupMenuAt(index, popupMenu);
    }

    public JPopupMenu getPopupMenuAt(int index) {
        checkIndex(index);
        return paneUI.getPopupMenuAt(index);
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        paneUI.setPopupMenu(popupMenu);
    }

    public JPopupMenu getPopupMenu() {
        return paneUI.getPopupMenu();
    }

    public void detachTab(int index) {
        checkIndex(index);
        paneUI.detachTab(index);
    }

    public void addCloseListener(CloseListener l) {
        listenerList.add(CloseListener.class, l);
    }

    public void addDetachListener(DetachListener l) {
        listenerList.add(DetachListener.class, l);
    }

    public void addDoubleClickListener(DoubleClickListener l) {
        listenerList.add(DoubleClickListener.class, l);
    }

    public void addPopupOutsideListener(PopupOutsideListener l) {
        listenerList.add(PopupOutsideListener.class, l);
    }

    public void removeCloseListener(CloseListener l) {
        listenerList.remove(CloseListener.class, l);
    }

    public void removeDetachListener(DetachListener l) {
        listenerList.remove(DetachListener.class, l);
    }

    public void removeDoubleClickListener(DoubleClickListener l) {
        listenerList.remove(DoubleClickListener.class, l);
    }

    public void removePopupOutsideListener(PopupOutsideListener l) {
        listenerList.remove(PopupOutsideListener.class, l);
    }


    public void fireCloseTabEvent(MouseEvent e, int overTabIndex) {
        TabbedEvent event = new TabbedEvent(this, e, null, overTabIndex);

        for (CloseListener closeListener : getListeners(CloseListener.class)) {
            closeListener.closeOperation(event);
        }
    }

    public void fireDetachTabEvent(MouseEvent e, int overTabIndex) {
        TabbedEvent event = new TabbedEvent(this, e, null, overTabIndex);

        DetachListener detachListeners[] = getListeners(DetachListener.class);
        for (DetachListener detachListener : detachListeners) {
            detachListener.detachOperation(event);
        }
    }

    public void fireDoubleClickTabEvent(MouseEvent e, int overTabIndex) {
        EventListener dClickListeners[] = getListeners(DoubleClickListener.class);
        for (EventListener dClickListener : dClickListeners) {
            ((DoubleClickListener) dClickListener).doubleClickOperation(e);
        }
    }

    public void firePopupOutsideTabEvent(MouseEvent e) {
        PopupOutsideListener popupListeners[] = getListeners(PopupOutsideListener.class);
        for (PopupOutsideListener popupListener : popupListeners) {
            popupListener.popupOutsideOperation(e);
        }
    }


    protected void checkIndex(int index) {
        if (index < 0 || index >= getTabCount())
            throw new IndexOutOfBoundsException("Index: " + index + ", Content count: " + getTabCount());
    }

}

