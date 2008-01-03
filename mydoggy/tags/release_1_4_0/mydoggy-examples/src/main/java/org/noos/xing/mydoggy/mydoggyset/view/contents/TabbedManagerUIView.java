package org.noos.xing.mydoggy.mydoggyset.view.contents;

import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyTabbedContentManagerUI;
import org.noos.xing.yasaf.plaf.component.MatrixPanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TabbedManagerUIView extends ComponentView implements ViewContextChangeListener {
    protected ToolWindowManager toolWindowManager;
    protected TabbedContentManagerUI tabbedContentManagerUI;
    protected JComboBox tabPlaces;
    protected JComboBox tabLayouts;
    protected JCheckBox isShowAlwaysTab;
    boolean valueChanging;

    public TabbedManagerUIView(ViewContext viewContext) {
        super(viewContext);
        this.toolWindowManager = viewContext.get(ToolWindowManager.class);

        ContentManagerUI contentManagerUI = toolWindowManager.getContentManager().getContentManagerUI();
        if (contentManagerUI instanceof MyDoggyTabbedContentManagerUI)
            this.tabbedContentManagerUI = (TabbedContentManagerUI) contentManagerUI;
        else
            this.tabbedContentManagerUI = new MyDoggyTabbedContentManagerUI();

        tabbedContentManagerUI.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (valueChanging)
                    return;

                String pName = evt.getPropertyName();
                if ("tabPlacement".equals(pName)) {
                    tabPlaces.setSelectedIndex(((TabbedContentManagerUI.TabPlacement) evt.getNewValue()).ordinal());
                } else if ("tabLayout".equals(pName)) {
                    tabLayouts.setSelectedIndex(((TabbedContentManagerUI.TabLayout) evt.getNewValue()).ordinal());
                } else if ("isShowAlwaysTab".equals(pName)) {
                    isShowAlwaysTab.setSelected((Boolean) evt.getNewValue());
                }
            }
        });
    }

    protected Component initComponent() {
        this.toolWindowManager = viewContext.get(ToolWindowManager.class);

        MatrixPanel panel = new MatrixPanel(3, 1);

        // Tab Placement
        panel.add(new JLabel(), "1,1,r,c");
        tabPlaces = new JComboBox(new Object[]{
                TabbedContentManagerUI.TabPlacement.TOP,
                TabbedContentManagerUI.TabPlacement.LEFT,
                TabbedContentManagerUI.TabPlacement.BOTTOM,
                TabbedContentManagerUI.TabPlacement.RIGHT
        });
        tabPlaces.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    valueChanging = true;
                    ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).setTabPlacement(
                            (TabbedContentManagerUI.TabPlacement) tabPlaces.getSelectedItem()
                    );
                    valueChanging = false;
                }
            }
        });
        panel.addEntry(0, 0, "Tab Placement : ", tabPlaces);

        // Tab Layout
        tabLayouts = new JComboBox(new Object[]{
                TabbedContentManagerUI.TabLayout.SCROLL,
                TabbedContentManagerUI.TabLayout.WRAP
        });
        tabLayouts.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    valueChanging = true;
                    ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).setTabLayout(
                            (TabbedContentManagerUI.TabLayout) tabLayouts.getSelectedItem()
                    );
                    valueChanging = false;
                }
            }
        });
        panel.addEntry(1, 0, "Tab Layout : ", tabLayouts);

        // isShowAlwaysTab
        isShowAlwaysTab = new JCheckBox();
        isShowAlwaysTab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).setShowAlwaysTab(
                        isShowAlwaysTab.isSelected()
                );
            }
        });
        panel.addEntry(2, 0, "IsShowAlwaysTab : ", isShowAlwaysTab);

        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ContentManagerUI.class.equals(evt.getProperty())) {
            if (evt.getNewValue().equals(TabbedContentManagerUI.class)) {
                toolWindowManager.getContentManager().setContentManagerUI(tabbedContentManagerUI);
                viewContext.put(ContentManagerUI.class, this);

                TabbedContentManagerUI tabbedContentManagerUI = ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI());
                tabPlaces.setSelectedIndex(tabbedContentManagerUI.getTabPlacement().ordinal());
                tabLayouts.setSelectedIndex(tabbedContentManagerUI.getTabLayout().ordinal());
                isShowAlwaysTab.setSelected(tabbedContentManagerUI.isShowAlwaysTab());
            }
        }
    }

}