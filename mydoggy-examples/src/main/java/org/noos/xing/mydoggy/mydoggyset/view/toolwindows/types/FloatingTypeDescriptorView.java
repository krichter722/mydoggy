package org.noos.xing.mydoggy.mydoggyset.view.toolwindows.types;

import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.yasaf.plaf.action.ChangeListenerAction;
import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.plaf.action.ViewContextSource;
import org.noos.xing.yasaf.plaf.bean.ChecBoxSelectionSource;
import org.noos.xing.yasaf.plaf.bean.SpinnerValueSource;
import org.noos.xing.yasaf.plaf.bean.ToFloatSource;
import org.noos.xing.yasaf.plaf.component.MatrixPanel;
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
    private JCheckBox enabledBox, modal, animating, idVisibleOnTitleBar, transparentMode,
            addToTaskBar, alwaysOnTop, osDecorated, resizable;
    private JSpinner transparentDelay, transparentRatio;

    public FloatingTypeDescriptorView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        MatrixPanel panel = new MatrixPanel(6, 2);

        // Left
        panel.addEntry(0, 0, "enabled : ", enabledBox = new JCheckBox());
        enabledBox.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                                               "enabled",
                                               new ViewContextSource(viewContext, ToolWindowTypeDescriptor.class),
                                               new ChecBoxSelectionSource(enabledBox)));

        panel.addEntry(1, 0, "modal : ", modal = new JCheckBox());
        modal.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                          "modal",
                                          new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                          new ChecBoxSelectionSource(modal)));

        panel.addEntry(2, 0, "animating : ", animating = new JCheckBox());
        animating.setSelected(true);
        animating.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                                              "animating",
                                              new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                              new ChecBoxSelectionSource(animating)));

        panel.addEntry(3, 0, "idVisibleOnTitleBar : ", idVisibleOnTitleBar = new JCheckBox());
        idVisibleOnTitleBar.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                                                        "idVisibleOnTitleBar",
                                                        new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                                        new ChecBoxSelectionSource(idVisibleOnTitleBar)));

        panel.addEntry(4, 0, "alwaysOnTop : ", alwaysOnTop = new JCheckBox());
        alwaysOnTop.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                                "alwaysOnTop",
                                                new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                                new ChecBoxSelectionSource(alwaysOnTop)));

        panel.addEntry(5, 0, "resizable : ", resizable = new JCheckBox());
        resizable.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                                "resizable",
                                                new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                                new ChecBoxSelectionSource(resizable)));

        // Right
        panel.add(new JLabel("transparentMode : "), "5,1,r,c");
        panel.add(transparentMode = new JCheckBox(), "7,1,FULL,FULL");
        transparentMode.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                                    "transparentMode",
                                                    new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                                    new ChecBoxSelectionSource(transparentMode)));

        panel.add(new JLabel("transparentDelay : "), "5,3,r,c");
        panel.add(transparentDelay = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 500)), "7,3,FULL,FULL");
        transparentDelay.addChangeListener(
                new ChangeListenerAction(FloatingTypeDescriptor.class,
                                         "transparentDelay",
                                         new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                         new SpinnerValueSource(transparentDelay))
        );

        panel.add(new JLabel("transparentRatio : "), "5,5,r,c");
        panel.add(transparentRatio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05)), "7,5,FULL,FULL");
        transparentRatio.addChangeListener(
                new ChangeListenerAction(FloatingTypeDescriptor.class,
                                         "transparentRatio",
                                         new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                         new ToFloatSource(new SpinnerValueSource(transparentRatio)))
        );

        panel.addEntry(3, 1, "addToTaskBar : ", addToTaskBar = new JCheckBox());
        addToTaskBar.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                                 "addToTaskBar",
                                                 new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                                 new ChecBoxSelectionSource(addToTaskBar)));

        panel.addEntry(4, 1, "osDecorated : ", osDecorated = new JCheckBox());
        osDecorated.setAction(new DynamicAction(FloatingTypeDescriptor.class,
                                                "osDecorated",
                                                new ViewContextSource(viewContext, FloatingTypeDescriptor.class),
                                                new ChecBoxSelectionSource(osDecorated)));
        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
            if (FloatingTypeDescriptor.class.equals(evt.getNewValue())) {
                ToolWindow toolWindow = viewContext.get(ToolWindow.class);
                FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
                viewContext.put(FloatingTypeDescriptor.class, descriptor);

                enabledBox.setSelected(descriptor.isEnabled());
                modal.setSelected(descriptor.isModal());
                animating.setSelected(descriptor.isAnimating());
                idVisibleOnTitleBar.setSelected(descriptor.isIdVisibleOnTitleBar());
                addToTaskBar.setSelected(descriptor.isAddToTaskBar());
                alwaysOnTop.setSelected(descriptor.isAlwaysOnTop());
                osDecorated.setSelected(descriptor.isOsDecorated());
                resizable.setSelected(descriptor.isResizable());

                transparentMode.setSelected(descriptor.isTransparentMode());
                transparentDelay.setValue(descriptor.getTransparentDelay());
                transparentRatio.setValue(descriptor.getTransparentRatio());

                viewContext.put(ToolWindowTypeDescriptor.class, this);
            }
        }
    }

}