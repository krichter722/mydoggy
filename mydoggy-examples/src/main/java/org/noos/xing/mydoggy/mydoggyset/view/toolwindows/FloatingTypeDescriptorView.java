package org.noos.xing.mydoggy.mydoggyset.view.toolwindows;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.yasaf.plaf.bean.ChecBoxSelectionSource;
import org.noos.xing.yasaf.plaf.bean.ToFloatSource;
import org.noos.xing.yasaf.plaf.bean.SpinnerValueSource;
import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.plaf.action.ViewContextSource;
import org.noos.xing.yasaf.plaf.action.ChangeListenerAction;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingTypeDescriptorView extends ComponentView implements ViewContextChangeListener {
    private JCheckBox enabledBox, modal, transparentMode;
    private JSpinner transparentDelay, transparentRatio;

    public FloatingTypeDescriptorView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{3, -2, 3, -1, 3, -2, 3, -1, 3}, {-1, 20, 3, 20, 3, 20, 3, 20, -1}}));

        // Left
        panel.add(new JLabel("enabled : "), "1,1,r,c");
        panel.add(enabledBox = new JCheckBox(), "3,1,FULL,FULL");
        enabledBox.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                               "enabled",
                                               new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                               new ChecBoxSelectionSource(enabledBox)));

        panel.add(new JLabel("modal : "), "1,3,r,c");
        panel.add(modal = new JCheckBox(), "3,3,FULL,FULL");
        modal.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                          "modal",
                                          new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                          new ChecBoxSelectionSource(modal)));

        // Right
        panel.add(new JLabel("transparentMode : "), "5,1,r,c");
        panel.add(transparentMode = new JCheckBox(), "7,1,FULL,FULL");
        transparentMode.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                                    "transparentMode",
                                                    new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                                    new ChecBoxSelectionSource(transparentMode)));

        panel.add(new JLabel("transparentDelay : "), "5,3,r,c");
        panel.add(transparentDelay = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 500)), "7,3,FULL,FULL");

        panel.add(new JLabel("transparentRatio : "), "5,5,r,c");
        panel.add(transparentRatio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05)), "7,5,FULL,FULL");
        transparentRatio.addChangeListener(
                new ChangeListenerAction(FloatingTypeDescriptor.class,
                                         "transparentRatio",
                                         new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                         new ToFloatSource(new SpinnerValueSource(transparentRatio)))
        );

        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
            if (evt.getNewValue().equals(FloatingTypeDescriptor.class)) {
                ToolWindow toolWindow = viewContext.get(ToolWindow.class);
                FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
                viewContext.put(FloatingTypeDescriptor.class, descriptor);

                enabledBox.setSelected(descriptor.isEnabled());
                modal.setSelected(descriptor.isModal());
//            idVisibleOnToolBar.setSelected(descriptor.isIdVisibleOnTitleBar());
//            dockLength.setValue(descriptor.getDockLength());

                transparentMode.setSelected(descriptor.isTransparentMode());
                transparentDelay.setValue(descriptor.getTransparentDelay());
                transparentRatio.setValue(descriptor.getTransparentRatio());

                viewContext.put(ToolWindowTypeDescriptor.class, this);
            }
        }
    }

}