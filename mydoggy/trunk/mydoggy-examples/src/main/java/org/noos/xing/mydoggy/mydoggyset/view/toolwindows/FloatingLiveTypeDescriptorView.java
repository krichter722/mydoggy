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
import org.noos.xing.yasaf.plaf.component.MatrixPanel;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingLiveTypeDescriptorView extends ComponentView implements ViewContextChangeListener {
    private JCheckBox enabledBox, animating, transparentMode;
    private JSpinner transparentDelay, transparentRatio;

    public FloatingLiveTypeDescriptorView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        MatrixPanel panel = new MatrixPanel(2, 4);

        // Left
        panel.addPair(0, 0, "enabled : ", enabledBox = new JCheckBox());
        enabledBox.setAction(new DynamicAction(FloatingLiveTypeDescriptor.class,
                                               "enabled",
                                               new ViewContextSource(viewContext, FloatingLiveTypeDescriptor.class),
                                               new ChecBoxSelectionSource(enabledBox)));

        panel.addPair(0, 2, "animating : ", animating = new JCheckBox());
        animating.setSelected(true);
        animating.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                                              "animating",
                                              new ViewContextSource(viewContext, FloatingLiveTypeDescriptor.class),
                                              new ChecBoxSelectionSource(animating)));

        // Right
        panel.add(new JLabel("transparentMode : "), "5,1,r,c");
        panel.add(transparentMode = new JCheckBox(), "7,1,FULL,FULL");
        transparentMode.setAction(new DynamicAction(FloatingLiveTypeDescriptor.class,
                                                    "transparentMode",
                                                    new ViewContextSource(viewContext, FloatingLiveTypeDescriptor.class),
                                                    new ChecBoxSelectionSource(transparentMode)));

        panel.add(new JLabel("transparentDelay : "), "5,3,r,c");
        panel.add(transparentDelay = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 500)), "7,3,FULL,FULL");
        transparentDelay.addChangeListener(
                new ChangeListenerAction(FloatingLiveTypeDescriptor.class,
                                         "transparentDelay",
                                         new ViewContextSource(viewContext, FloatingLiveTypeDescriptor.class),
                                         new SpinnerValueSource(transparentDelay))
        );

        panel.add(new JLabel("transparentRatio : "), "5,5,r,c");
        panel.add(transparentRatio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05)), "7,5,FULL,FULL");
        transparentRatio.addChangeListener(
                new ChangeListenerAction(FloatingLiveTypeDescriptor.class,
                                         "transparentRatio",
                                         new ViewContextSource(viewContext, FloatingLiveTypeDescriptor.class),
                                         new ToFloatSource(new SpinnerValueSource(transparentRatio)))
        );

        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
            if (evt.getNewValue().equals(FloatingLiveTypeDescriptor.class)) {
                ToolWindow toolWindow = viewContext.get(ToolWindow.class);
                FloatingLiveTypeDescriptor descriptor = (FloatingLiveTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
                viewContext.put(FloatingLiveTypeDescriptor.class, descriptor);

                enabledBox.setSelected(descriptor.isEnabled());
                animating.setSelected(descriptor.isAnimating());

                transparentMode.setSelected(descriptor.isTransparentMode());
                transparentDelay.setValue(descriptor.getTransparentDelay());
                transparentRatio.setValue(descriptor.getTransparentRatio());

                viewContext.put(ToolWindowTypeDescriptor.class, this);
            }
        }
    }

}