package org.noos.xing.mydoggy.examples.mydoggyset.view.contents;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TabbedManagerUIView extends ComponentView implements ViewContextChangeListener {
    protected ToolWindowManager toolWindowManager;
    protected TabbedContentManagerUI tabbedContentManagerUI;

    protected JComboBox tabPlaces;
    protected JComboBox tabLayouts;
    boolean valueChanging;

    public TabbedManagerUIView(ViewContext viewContext) {
        super(viewContext);
        this.toolWindowManager = viewContext.get(ToolWindowManager.class);
        this.tabbedContentManagerUI = (TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI();

        tabbedContentManagerUI.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (valueChanging)
                    return;

                String pName = evt.getPropertyName();
                if ("tabPlacement".equals(pName)) {
                    tabPlaces.setSelectedIndex(((TabbedContentManagerUI.TabPlacement)evt.getNewValue()).ordinal());
                } else if ("tabLayout".equals(pName)) {
                    tabLayouts.setSelectedIndex(((TabbedContentManagerUI.TabLayout)evt.getNewValue()).ordinal());
                }
            }
        });
    }

    protected Component initComponent() {
        this.toolWindowManager = viewContext.get(ToolWindowManager.class);

        JPanel panel = new JPanel(new TableLayout(new double[][]{{3, 120, 3, -1, 3},{3, 20, 3, 20, 3, 20, 3}}));

        // Tab Placement
        panel.add(new JLabel("Tab Placement : "), "1,1,r,c");
        tabPlaces = new JComboBox(new Object[]{
                TabbedContentManagerUI.TabPlacement.TOP,
                TabbedContentManagerUI.TabPlacement.LEFT,
                TabbedContentManagerUI.TabPlacement.BOTTOM,
                TabbedContentManagerUI.TabPlacement.RIGHT
        });
        tabPlaces.setSelectedIndex(
                ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).getTabPlacement().ordinal()
        );
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
        panel.add(tabPlaces, "3,1,FULL,FULL");

        // Tab Layout
        panel.add(new JLabel("Tab Layout : "), "1,3,r,c");
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
        tabLayouts.setSelectedIndex(
                ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).getTabLayout().ordinal()
        );
        panel.add(tabLayouts, "3,3,FULL,FULL");

        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ContentManagerUI.class.equals(evt.getProperty())) {
            if (evt.getNewValue().equals(TabbedContentManagerUI.class)) {
                toolWindowManager.getContentManager().setContentManagerUI(tabbedContentManagerUI);
                viewContext.put(ContentManagerUI.class, this);
            }
        }
    }


}