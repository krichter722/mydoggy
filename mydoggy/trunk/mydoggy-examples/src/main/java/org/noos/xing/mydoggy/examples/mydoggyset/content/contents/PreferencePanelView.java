package org.noos.xing.mydoggy.examples.mydoggyset.content.contents;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.examples.mydoggyset.content.toolwindows.DockedTypeDescriptorView;
import org.noos.xing.mydoggy.examples.mydoggyset.content.toolwindows.SlidingTypeDescriptorView;
import org.noos.xing.mydoggy.examples.mydoggyset.content.toolwindows.FloatingTypeDescriptorView;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.PanelViewContainer;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContainer;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PreferencePanelView extends ComponentView {
    protected JPanel preferencePanel;
    protected ViewContainer uisContainer;

    public PreferencePanelView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        preferencePanel = new JPanel(new TableLayout(new double[][]{{-1}, {20, 3, -1}}));
        preferencePanel.setBorder(new TitledBorder("Preferences"));

        uisContainer = new PanelViewContainer() {
            protected void initComponents() {
                super.initComponents();
                panel.setBorder(new LineBorder(Color.DARK_GRAY));
            }
        };

        preferencePanel.add(uisContainer.getContainer(), "0,2,FULL,FULL");
        preferencePanel.add(new UIsSelectorView(viewContext).getComponent(), "0,0,FULL,FULL");

        return preferencePanel;
    }

    protected void initListeners() {
        viewContext.addViewContextChangeListener(new TabbedManagerUIView(viewContext));
        viewContext.addViewContextChangeListener(new DesktopManagerUIView(viewContext));

        viewContext.addViewContextChangeListener(new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                if (ContentManagerUI.class.equals(evt.getProperty())) {
                    if (evt.getNewValue() instanceof View) {
                        uisContainer.plugView((View) evt.getNewValue());
                    }
                }
            }
        });
    }

    protected class UIsSelectorView extends ComponentView {
        protected JComboBox uis;

        public UIsSelectorView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JPanel panel = new JPanel(new TableLayout(new double[][]{{150, 3, -1}, {-1}}));

            uis = new JComboBox(new Object[]{
                    TabbedContentManagerUI.class,
                    DesktopContentManagerUI.class
            });

            uis.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    viewContext.put(ContentManagerUI.class, e.getItem());
                }
            });

            uis.setSelectedIndex(0);

            panel.add(new JLabel("ContentManagerUI : "), "0,0,r,FULL");
            panel.add(uis, "2,0,FULL,FULL");

            return panel;
        }

    }
}