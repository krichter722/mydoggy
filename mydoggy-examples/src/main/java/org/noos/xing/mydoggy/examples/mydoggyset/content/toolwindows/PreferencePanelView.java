package org.noos.xing.mydoggy.examples.mydoggyset.content.toolwindows;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
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
    protected ViewContainer typeDescriptorContainer;

    public PreferencePanelView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        preferencePanel = new JPanel(new TableLayout(new double[][]{{-1}, {20, 3, -1}}));
        preferencePanel.setBorder(new TitledBorder("Preferences"));


        typeDescriptorContainer = new PanelViewContainer() {
            protected void initComponents() {
                super.initComponents();
                panel.setBorder(new LineBorder(Color.DARK_GRAY));
            }
        };

        preferencePanel.add(typeDescriptorContainer.getContainer(), "0,2,FULL,FULL");
        preferencePanel.add(new TypeDescriptorSelectorView(viewContext).getComponent(), "0,0,FULL,FULL");

        return preferencePanel;
    }

    protected void initListeners() {
        viewContext.addViewContextChangeListener(new DockedTypeDescriptorView(viewContext));
        viewContext.addViewContextChangeListener(new SlidingTypeDescriptorView(viewContext));
        viewContext.addViewContextChangeListener(new FloatingTypeDescriptorView(viewContext));

        viewContext.addViewContextChangeListener(new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                if (ToolWindow.class.equals(evt.getProperty())) {
                    ToolWindow toolWindow = (ToolWindow) evt.getNewValue();

                    preferencePanel.setBorder(new TitledBorder("Preference : " + toolWindow.getId()));
                } else if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
                    if (evt.getNewValue() instanceof View) {
                        typeDescriptorContainer.plugView((View) evt.getNewValue());
                    }
                }
            }
        });
    }

    protected class TypeDescriptorSelectorView extends ComponentView {
        protected JComboBox types;

        public TypeDescriptorSelectorView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JPanel panel = new JPanel(new TableLayout(new double[][]{{150, 3, -1}, {-1}}));

            types = new JComboBox(new Object[]{
                    null,
                    DockedTypeDescriptor.class,
                    SlidingTypeDescriptor.class,
                    FloatingTypeDescriptor.class
            });

            types.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    viewContext.put(ToolWindowTypeDescriptor.class, e.getItem());
                }
            });

            panel.add(new JLabel("Type Descriptor : "), "0,0,r,FULL");
            panel.add(types, "2,0,FULL,FULL");

            return panel;
        }

        protected void initListeners() {
            viewContext.addViewContextChangeListener(new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    if (ToolWindow.class.equals(evt.getProperty())) {
                        types.setSelectedIndex(0);
                        types.setSelectedIndex(1);
                    }
                }
            });
        }

    }
}
