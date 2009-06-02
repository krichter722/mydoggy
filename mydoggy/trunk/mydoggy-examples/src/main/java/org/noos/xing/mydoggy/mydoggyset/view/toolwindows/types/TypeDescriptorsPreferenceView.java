package org.noos.xing.mydoggy.mydoggyset.view.toolwindows.types;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.PanelViewContainer;
import org.noos.xing.yasaf.plaf.view.listener.ContextPutItemListener;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContainer;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TypeDescriptorsPreferenceView extends ComponentView {
    protected JPanel mainPanel;
    protected ViewContainer typeDescriptorContainer;

    public TypeDescriptorsPreferenceView(ViewContext viewContext) {
        super(viewContext);
    }


    protected Component initComponent() {
        mainPanel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {20, 3, -1}}));

        typeDescriptorContainer = new TypeDescriptorContainer();

        mainPanel.add(typeDescriptorContainer.getContainer(), "0,2,FULL,FULL");
        mainPanel.add(new TypeDescriptorSelectorView(viewContext).getComponent(), "0,0,FULL,FULL");

        return mainPanel;
    }

    protected void initListeners() {
        viewContext.addViewContextChangeListener(new NullTypeDescriptorView(viewContext));
        viewContext.addViewContextChangeListener(new DockedTypeDescriptorView(viewContext));
        viewContext.addViewContextChangeListener(new SlidingTypeDescriptorView(viewContext));
        viewContext.addViewContextChangeListener(new FloatingTypeDescriptorView(viewContext));
        viewContext.addViewContextChangeListener(new FloatingLiveTypeDescriptorView(viewContext));

        viewContext.addViewContextChangeListener(new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
                    if (evt.getNewValue() instanceof View)
                        typeDescriptorContainer.plugView((View) evt.getNewValue());
                }
            }
        });

    }


    protected class TypeDescriptorContainer extends PanelViewContainer {

        protected void initComponents() {
            super.initComponents();
            ((JComponent) panel).setBorder(new LineBorder(Color.DARK_GRAY));
        }

    }

    protected class TypeDescriptorSelectorView extends ComponentView {
        protected JComboBox types;

        public TypeDescriptorSelectorView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{150, 3, -1, 5 , 100}, {-1}}));

            types = new JComboBox(new Object[]{
                    "<none>",
                    DockedTypeDescriptor.class,
                    SlidingTypeDescriptor.class,
                    FloatingTypeDescriptor.class,
                    FloatingLiveTypeDescriptor.class
            });
            types.addItemListener(new ContextPutItemListener(viewContext, ToolWindowTypeDescriptor.class));
            types.setEnabled(false);

            panel.add(new JLabel("Type Descriptor : "), "0,0,r,FULL");
            panel.add(types, "2,0,FULL,FULL");
            panel.add(new JButton(new ViewContextAction("Remove", null, viewContext, "remove", ToolWindow.class)), "4,0,FULL,FULL");

            return panel;
        }

        protected void initListeners() {
            viewContext.addViewContextChangeListener(ToolWindow.class, new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    types.setSelectedIndex(0);
                    if (evt.getNewValue() != null)
                        types.setSelectedIndex(1);
                    types.setEnabled(evt.getNewValue() != null);
                }
            });
            viewContext.addViewContextChangeListener("remove", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    evt.getViewContext().get(ToolWindowManager.class).unregisterToolWindow(
                        evt.getViewContext().get(ToolWindow.class).getId()
                    );
                    evt.getViewContext().put(ToolWindow.class, null);
                }
            });
        }

    }
}