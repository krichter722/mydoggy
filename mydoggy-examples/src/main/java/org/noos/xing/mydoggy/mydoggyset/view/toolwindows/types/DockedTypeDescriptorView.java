package org.noos.xing.mydoggy.mydoggyset.view.toolwindows.types;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.yasaf.plaf.action.ChangeListenerAction;
import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.plaf.action.ViewContextSource;
import org.noos.xing.yasaf.plaf.bean.ChecBoxSelectionSource;
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
public class DockedTypeDescriptorView extends ComponentView implements ViewContextChangeListener {
    private JCheckBox popupMenuEnabled, hideLabelOnVisible, idVisibleOnTitleBar, animating;
    private JSpinner dockLength;

    public DockedTypeDescriptorView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        MatrixPanel panel = new MatrixPanel(4, 2);

        // Left
        panel.add(new JLabel("popupMenuEnabled : "), "1,1,r,c");
        panel.add(popupMenuEnabled = new JCheckBox(), "3,1,FULL,FULL");
        popupMenuEnabled.setAction(new DynamicAction(DockedTypeDescriptor.class,
                                                     "popupMenuEnabled",
                                                     new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                                     new ChecBoxSelectionSource(popupMenuEnabled)));

        panel.add(new JLabel("hideRepresentativeButtonOnVisible : "), "1,3,r,c");
        panel.add(hideLabelOnVisible = new JCheckBox(), "3,3,FULL,FULL");
        hideLabelOnVisible.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                                                       "hideRepresentativeButtonOnVisible",
                                                       new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                                       new ChecBoxSelectionSource(hideLabelOnVisible)));

        panel.add(new JLabel("idVisibleOnTitleBar : "), "1,5,r,c");
        panel.add(idVisibleOnTitleBar = new JCheckBox(), "3,5,FULL,FULL");
        idVisibleOnTitleBar.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                                                        "idVisibleOnTitleBar",
                                                        new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                                        new ChecBoxSelectionSource(idVisibleOnTitleBar)));

        panel.add(new JLabel("dockLength : "), "1,7,r,c");
        panel.add(dockLength = new JSpinner(new SpinnerNumberModel(100, 100, 400, 10)), "3,7,FULL,FULL");
        dockLength.addChangeListener(
                new ChangeListenerAction(DockedTypeDescriptor.class,
                                         "dockLength",
                                         new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                         new SpinnerValueSource(dockLength))
        );

        // Right

        panel.addEntry(3, 1, "animating : ", animating = new JCheckBox());
        animating.setSelected(true);
        animating.setAction(new DynamicAction(ToolWindowTypeDescriptor.class,
                                              "animating",
                                              new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                              new ChecBoxSelectionSource(animating)));
        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
            if (DockedTypeDescriptor.class.equals(evt.getNewValue())) {
                ToolWindow toolWindow = viewContext.get(ToolWindow.class);

                if (toolWindow == null)
                    return;


                DockedTypeDescriptor descriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
                viewContext.put(DockedTypeDescriptor.class, descriptor);

                popupMenuEnabled.setSelected(descriptor.isPopupMenuEnabled());
                hideLabelOnVisible.setSelected(descriptor.isHideRepresentativeButtonOnVisible());
                idVisibleOnTitleBar.setSelected(descriptor.isIdVisibleOnTitleBar());
                dockLength.setValue(descriptor.getDockLength());

                animating.setSelected(descriptor.isAnimating());

                viewContext.put(ToolWindowTypeDescriptor.class, this);
            }
        }
    }


}