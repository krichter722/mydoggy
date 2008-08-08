package org.noos.xing.mydoggy.mydoggyset.view.toolwindows.types;

import org.noos.xing.mydoggy.SlidingTypeDescriptor;
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
public class SlidingTypeDescriptorView extends ComponentView implements ViewContextChangeListener {
    private JCheckBox enabled, animating, idVisibleOnTitleBar, transparentMode;
    private JSpinner transparentDelay, transparentRatio;

    public SlidingTypeDescriptorView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        MatrixPanel panel = new MatrixPanel(3, 2);

        panel.addEntry(0, 0, "enabled : ", enabled = new JCheckBox());
        enabled.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                "enabled",
                new ViewContextSource(viewContext, ToolWindowTypeDescriptor.class),
                new ChecBoxSelectionSource(enabled)));

        panel.addEntry(1, 0, "animating : ", animating = new JCheckBox());
        animating.setSelected(true);
        animating.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                "animating",
                new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                new ChecBoxSelectionSource(animating)));

        panel.addEntry(2, 0, "idVisibleOnTitleBar : ", idVisibleOnTitleBar = new JCheckBox());
        idVisibleOnTitleBar.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                "idVisibleOnTitleBar",
                new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                new ChecBoxSelectionSource(idVisibleOnTitleBar)));


        panel.addEntry(0, 1, "transparentMode : ", transparentMode = new JCheckBox());
        transparentMode.setAction(new DynamicAction(SlidingTypeDescriptor.class,
                "transparentMode",
                new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                new ChecBoxSelectionSource(transparentMode)));

        panel.addEntry(1, 1, "transparentRatio : ", transparentRatio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05)));
        transparentRatio.addChangeListener(
                new ChangeListenerAction(SlidingTypeDescriptor.class,
                        "transparentRatio",
                        new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                        new ToFloatSource(new SpinnerValueSource(transparentRatio)))
        );

        panel.addEntry(2, 1, "transparentDelay : ", transparentDelay = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 500)));
        transparentDelay.addChangeListener(
                new ChangeListenerAction(SlidingTypeDescriptor.class,
                        "transparentDelay",
                        new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                        new SpinnerValueSource(transparentDelay))
        );

        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
            if (SlidingTypeDescriptor.class.equals(evt.getNewValue())) {
                ToolWindow toolWindow = viewContext.get(ToolWindow.class);
                SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
                viewContext.put(SlidingTypeDescriptor.class, descriptor);

                enabled.setSelected(descriptor.isEnabled());
                animating.setSelected(descriptor.isAnimating());
                idVisibleOnTitleBar.setSelected(descriptor.isIdVisibleOnTitleBar());

                transparentMode.setSelected(descriptor.isTransparentMode());
                transparentDelay.setValue(descriptor.getTransparentDelay());
                transparentRatio.setValue(descriptor.getTransparentRatio());

                viewContext.put(ToolWindowTypeDescriptor.class, this);
            }
        }
    }

}