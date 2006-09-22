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

    private JTabbedPane tabbedPane;
    private Component singleComponent;
    private boolean showAlwaysTab;

    public MyDoggyWindowContentManager(MyDoggyToolWindowManager windowManager) {
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
            if (tabbedPane.getTabCount() == 1 && windowManager.getMainContent() != tabbedPane) {
                if (singleComponent != null) {
                    tabbedPane.setComponentAt(0, singleComponent);
                    singleComponent = null;
                }
                windowManager.setMainContent(tabbedPane);
            }
        } else {
            // TODO: implement this  
        }
    }


    public int getContentCount() {
        return tabbedPane.getTabCount();
    }

    public void addContent(String title, Icon icon, Component component, String tip) {
        if (!showAlwaysTab) {
            tabbedPane.addTab(title, icon, new JPanel(), tip);

            if (tabbedPane.getTabCount() == 1) {
                singleComponent = component;

                windowManager.setMainContent(component);
            } else {
                if (singleComponent != null) {
                    tabbedPane.setComponentAt(0, singleComponent);
                    singleComponent = null;
                }
                tabbedPane.setComponentAt(tabbedPane.getTabCount() - 1, component);

                windowManager.setMainContent(tabbedPane);
            }
        } else {
            tabbedPane.addTab(title, icon, component, tip);
            windowManager.setMainContent(tabbedPane);
        }

        if (!tabbedPane.isEnabledAt(tabbedPane.getSelectedIndex())) {
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }

    }

    public void addContent(String title, Icon icon, Component component) {
        addContent(title, icon, component, "");
    }

    public void removeContentAt(int index) {
        tabbedPane.remove(index);
    }


    public Component getComponentAt(int index) {
        if (tabbedPane.getTabCount() == 1 && windowManager.getMainContent() != tabbedPane && index == 0)
            return windowManager.getMainContent(); 
        return tabbedPane.getComponentAt(index);
    }

    public Icon getDisabledIconAt(int index) {
        return tabbedPane.getDisabledIconAt(index);
    }

    public Icon getIconAt(int index) {
        return tabbedPane.getIconAt(index);
    }

    public String getTitleAt(int index) {
        return tabbedPane.getTitleAt(index);
    }

    public String getToolTipTextAt(int index) {
        return tabbedPane.getToolTipTextAt(index);
    }

    public boolean isEnabledAt(int index) {
        return tabbedPane.isEnabledAt(index);
    }


    public void setComponentAt(int index, Component component) {
        if (tabbedPane.getTabCount() == 1 && windowManager.getMainContent() != tabbedPane && index == 0) {
            singleComponent = component;
            windowManager.setMainContent(component);
        } else
            tabbedPane.setComponentAt(index, component);
    }

    public void setDisabledIconAt(int index, Icon disabledIcon) {
        tabbedPane.setDisabledIconAt(index, disabledIcon);
    }

    public void setEnabledAt(int index, boolean enabled) {
        if (tabbedPane.getSelectedIndex() == index && !enabled)
            tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1 % tabbedPane.getTabCount());

        tabbedPane.setEnabledAt(index, enabled);
    }

    public void setIconAt(int index, Icon icon) {
        tabbedPane.setIconAt(index, icon);
    }

    public void setTitleAt(int index, String title) {
        tabbedPane.setTitleAt(index, title);
    }

    public void setToolTipTextAt(int index, String toolTipText) {
        tabbedPane.setToolTipTextAt(index, toolTipText);
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

        this.tabbedPane = tabbedContentManager;
    }

}
