package org.noos.xing.mydoggy.mydoggyset.view.toolwindows;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.plaf.action.ViewContextSource;
import org.noos.xing.yasaf.plaf.action.ChangeListenerAction;
import org.noos.xing.yasaf.plaf.bean.ChecBoxSelectionSource;
import org.noos.xing.yasaf.plaf.bean.ToFloatSource;
import org.noos.xing.yasaf.plaf.bean.SpinnerValueSource;
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
    private JCheckBox enabled, animating, transparentMode;
    private JSpinner transparentDelay, transparentRatio;

    public SlidingTypeDescriptorView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        MatrixPanel panel = new MatrixPanel(2, 3);

        panel.addPair(0, 0, "enabled : ", enabled = new JCheckBox());
        enabled.setAction(new DynamicAction(SlidingTypeDescriptor.class,
                                            "enabled",
                                            new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                                            new ChecBoxSelectionSource(enabled)));

        panel.addPair(0, 1, "animating : ", animating = new JCheckBox());
        animating.setSelected(true);
        animating.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                                              "animating",
                                              new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                                              new ChecBoxSelectionSource(animating)));

        panel.addPair(1, 0, "transparentMode : ", transparentMode = new JCheckBox());
        transparentMode.setAction(new DynamicAction(SlidingTypeDescriptor.class,
                                                    "transparentMode",
                                                    new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                                                    new ChecBoxSelectionSource(transparentMode)));

        panel.addPair(1, 1, "transparentRatio : ", transparentRatio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05)));
        transparentRatio.addChangeListener(
                new ChangeListenerAction(SlidingTypeDescriptor.class,
                                         "transparentRatio",
                                         new ViewContextSource(viewContext, SlidingTypeDescriptor.class),
                                         new ToFloatSource(new SpinnerValueSource(transparentRatio)))
        );

        panel.addPair(1, 2, "transparentDelay : ", transparentDelay = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 500)));
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
            if (evt.getNewValue().equals(SlidingTypeDescriptor.class)) {
                ToolWindow toolWindow = viewContext.get(ToolWindow.class);
                SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
                viewContext.put(SlidingTypeDescriptor.class, descriptor);

                enabled.setSelected(descriptor.isEnabled());
                animating.setSelected(descriptor.isAnimating());
 
                transparentMode.setSelected(descriptor.isTransparentMode());
                transparentDelay.setValue(descriptor.getTransparentDelay());
                transparentRatio.setValue(descriptor.getTransparentRatio());

                viewContext.put(ToolWindowTypeDescriptor.class, this);
            }
        }
    }

}