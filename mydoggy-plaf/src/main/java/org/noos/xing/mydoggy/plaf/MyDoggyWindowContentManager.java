package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.ToolWindowContentManager;
import org.noos.xing.mydoggy.plaf.ui.content.CloseListener;
import org.noos.xing.mydoggy.plaf.ui.content.DetachListener;
import org.noos.xing.mydoggy.plaf.ui.content.JTabbedContentManager;
import org.noos.xing.mydoggy.plaf.ui.content.TabbedEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyWindowContentManager implements ToolWindowContentManager {
    private MyDoggyToolWindowManager windowManager;

    private JTabbedContentManager tabbedContentManager;
    private Component singleComponent;
    private boolean showAlwaysTab;

    MyDoggyWindowContentManager(MyDoggyToolWindowManager windowManager) {
        this.windowManager = windowManager;
        this.showAlwaysTab = false;

        initComponents();
    }


    public boolean isShowAlwaysTab() {
        return showAlwaysTab;
    }

    public void setShowAlwaysTab(boolean showAlwaysTab) {
        this.showAlwaysTab = showAlwaysTab;

        if (showAlwaysTab) {
            if (tabbedContentManager.getTabCount() == 1 && windowManager.getMainContent() != tabbedContentManager) {
                if (singleComponent != null) {
                    tabbedContentManager.setComponentAt(0, singleComponent);
                    singleComponent = null;
                }
                windowManager.setMainContent(tabbedContentManager);
            }
        } 
    }


    public int getContentCount() {
        return tabbedContentManager.getTabCount();
    }


    public void addContent(String title, Icon icon, Component component, String tip) {
        if (!showAlwaysTab) {
            tabbedContentManager.addTab(title, icon, new JPanel(), tip);

            if (tabbedContentManager.getTabCount() == 1) {
                singleComponent = component;

                windowManager.setMainContent(component);
            } else {
                if (singleComponent != null) {
                    tabbedContentManager.setComponentAt(0, singleComponent);
                    singleComponent = null;
                }
                tabbedContentManager.setComponentAt(tabbedContentManager.getTabCount() - 1, component);

                windowManager.setMainContent(tabbedContentManager);
            }
        } else {
            tabbedContentManager.addTab(title, icon, component, tip);
            windowManager.setMainContent(tabbedContentManager);
        }

        if (!tabbedContentManager.isEnabledAt(tabbedContentManager.getSelectedIndex())) {
            tabbedContentManager.setSelectedIndex(tabbedContentManager.getTabCount() - 1);
        }

    }

    public void addContent(String title, Icon icon, Component component) {
        addContent(title, icon, component, "");
    }

    public void removeContentAt(int index) {
        tabbedContentManager.remove(index);
    }


    public void setSelectedContent(int index) {
        tabbedContentManager.setSelectedIndex(index);
    }

    public int getSelectedContent() {
        return tabbedContentManager.getSelectedIndex();
    }


    public void setComponentAt(int index, Component component) {
        if (tabbedContentManager.getTabCount() == 1 && windowManager.getMainContent() != tabbedContentManager && index == 0) {
            singleComponent = component;
            windowManager.setMainContent(component);
        } else
            tabbedContentManager.setComponentAt(index, component);
    }

    public Component getComponentAt(int index) {
        if (tabbedContentManager.getTabCount() == 1 && windowManager.getMainContent() != tabbedContentManager && index == 0)
            return windowManager.getMainContent(); 
        return tabbedContentManager.getComponentAt(index);
    }

    public void setDisabledIconAt(int index, Icon disabledIcon) {
        tabbedContentManager.setDisabledIconAt(index, disabledIcon);
    }

    public Icon getDisabledIconAt(int index) {
        return tabbedContentManager.getDisabledIconAt(index);
    }

    public void setIconAt(int index, Icon icon) {
        tabbedContentManager.setIconAt(index, icon);
    }

    public Icon getIconAt(int index) {
        return tabbedContentManager.getIconAt(index);
    }

    public void setTitleAt(int index, String title) {
        tabbedContentManager.setTitleAt(index, title);
    }

    public String getTitleAt(int index) {
        return tabbedContentManager.getTitleAt(index);
    }

    public void setToolTipTextAt(int index, String toolTipText) {
        tabbedContentManager.setToolTipTextAt(index, toolTipText);
    }

    public String getToolTipTextAt(int index) {
        return tabbedContentManager.getToolTipTextAt(index);
    }

    public void setEnabledAt(int index, boolean enabled) {
        if (tabbedContentManager.getSelectedIndex() == index && !enabled)
            tabbedContentManager.setSelectedIndex(tabbedContentManager.getSelectedIndex() + 1 % tabbedContentManager.getTabCount());

        tabbedContentManager.setEnabledAt(index, enabled);
    }

    public boolean isEnabledAt(int index) {
        return tabbedContentManager.isEnabledAt(index);
    }

    public void setPopupMenuAt(int index, JPopupMenu popupMenu) {
        tabbedContentManager.setPopupMenuAt(index, popupMenu);
    }

    public JPopupMenu getPopupMenuAt(int index) {
        return tabbedContentManager.getPopupMenuAt(index);
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        tabbedContentManager.setPopupMenu(popupMenu);
    }

    public JPopupMenu getPopupMenu() {
        return tabbedContentManager.getPopupMenu();
    }


    protected void initComponents() {
        JTabbedContentManager tabbedContentManager = new JTabbedContentManager(
                (windowManager.getAnchestor() instanceof Frame) ? (Frame) windowManager.getAnchestor() : null
        );

        tabbedContentManager.addCloseListener(new CloseListener() {
            public void closeOperation(TabbedEvent e) {
                removeContentAt(e.getOverTabIndex());
            }
        });

        tabbedContentManager.addDetachListener(new DetachListener() {
            public void detachOperation(TabbedEvent e) {
                e.getContentManager().detachTab(e.getOverTabIndex());
            }
        });

        this.tabbedContentManager = tabbedContentManager;
    }

}
