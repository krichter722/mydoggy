package org.noos.xing.mydoggy.mydoggyset.view.toolwindows;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.SlidingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.yasaf.plaf.bean.ChecBoxSelectionSource;
import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.plaf.action.ViewContextSource;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SlidingTypeDescriptorView extends ComponentView implements ViewContextChangeListener {
    private JCheckBox enabled, transparentMode;
    private JTextField transparentRatio, transparentDelay;

    public SlidingTypeDescriptorView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        // TODO: add animating
        JPanel panel = new JPanel(new TableLayout(new double[][]{{3, -2, 3, -1, 3}, {-1, 20, 3, 20, 3, 20, 3, 20, -1}}));

        panel.add(new JLabel("enabled : "), "1,1,r,c");
        panel.add(enabled = new JCheckBox(), "3,1,FULL,FULL");
        enabled.setAction(new DynamicAction(SlidingTypeDescriptor.class,
                                            "enabled",
                                            new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                                            new ChecBoxSelectionSource(enabled)));

        panel.add(new JLabel("transparentMode : "), "1,3,r,c");
        panel.add(transparentMode = new JCheckBox(), "3,3,FULL,FULL");
        transparentMode.setAction(new DynamicAction(SlidingTypeDescriptor.class,
                                                    "transparentMode",
                                                    new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                                                    new ChecBoxSelectionSource(transparentMode)));

        panel.add(new JLabel("transparentRatio : "), "1,5,r,c");
        panel.add(transparentRatio = new JTextField(), "3,5,FULL,FULL");

        panel.add(new JLabel("transparentDelay : "), "1,7,r,c");
        panel.add(transparentDelay = new JTextField(), "3,7,FULL,FULL");

        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
            if (evt.getNewValue().equals(SlidingTypeDescriptor.class)) {
                ToolWindow toolWindow = viewContext.get(ToolWindow.class);
                SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
                viewContext.put(SlidingTypeDescriptor.class, descriptor);

                enabled.setSelected(descriptor.isEnabled());

                transparentMode.setSelected(descriptor.isTransparentMode());
                transparentDelay.setText(String.valueOf(descriptor.getTransparentDelay()));
                transparentRatio.setText(String.valueOf(descriptor.getTransparentRatio()));

                viewContext.put(ToolWindowTypeDescriptor.class, this);
            }
        }
    }

}