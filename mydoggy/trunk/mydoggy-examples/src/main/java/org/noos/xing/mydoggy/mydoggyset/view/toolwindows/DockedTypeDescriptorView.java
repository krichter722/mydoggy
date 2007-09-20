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
public class DockedTypeDescriptorView extends ComponentView implements ViewContextChangeListener {
    private JCheckBox popupMenuEnabled, hideLabelOnVisible, idVisibleOnTitleBar, previewEnabled;
    private JSpinner dockLength, previewDelay, previewTransparentRatio;

    public DockedTypeDescriptorView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{3, -2, 3, -1, 3, -2, 3, -1, 3}, {-1, 20, 3, 20, 3, 20, 3, 20, -1}}));

        // Left
        panel.add(new JLabel("PopupMenuEnabled : "), "1,1,r,c");
        panel.add(popupMenuEnabled = new JCheckBox(), "3,1,FULL,FULL");
        popupMenuEnabled.setAction(new DynamicAction(DockedTypeDescriptor.class,
                                                     "popupMenuEnabled",
                                                     new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                                     new ChecBoxSelectionSource(popupMenuEnabled)));

        panel.add(new JLabel("HideLabelOnVisible : "), "1,3,r,c");
        panel.add(hideLabelOnVisible = new JCheckBox(), "3,3,FULL,FULL");
        hideLabelOnVisible.setAction(new DynamicAction(DockedTypeDescriptor.class,
                                                       "hideRepresentativeButtonOnVisible",
                                                       new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                                       new ChecBoxSelectionSource(hideLabelOnVisible)));

        panel.add(new JLabel("IdVisibleOnToolBar : "), "1,5,r,c");
        panel.add(idVisibleOnTitleBar = new JCheckBox(), "3,5,FULL,FULL");
        idVisibleOnTitleBar.setAction(new DynamicAction(DockedTypeDescriptor.class,
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
        panel.add(new JLabel("previewEnabled : "), "5,1,r,c");
        panel.add(previewEnabled = new JCheckBox(), "7,1,FULL,FULL");
        previewEnabled.setAction(new DynamicAction(DockedTypeDescriptor.class,
                                                   "previewEnabled",
                                                   new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                                   new ChecBoxSelectionSource(previewEnabled)));

        panel.add(new JLabel("previewDelay : "), "5,3,r,c");
        panel.add(previewDelay = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 500)), "7,3,FULL,FULL");
        previewDelay.addChangeListener(
                new ChangeListenerAction(DockedTypeDescriptor.class,
                                         "previewDelay",
                                         new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                         new SpinnerValueSource(previewDelay))
        );

        panel.add(new JLabel("previewTransparentRatio : "), "5,5,r,c");
        panel.add(previewTransparentRatio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05)), "7,5,FULL,FULL");
        previewTransparentRatio.addChangeListener(
                new ChangeListenerAction(DockedTypeDescriptor.class,
                                         "previewTransparentRatio",
                                         new ViewContextSource(viewContext, DockedTypeDescriptor.class),
                                         new ToFloatSource(new SpinnerValueSource(previewTransparentRatio)))
        );

        return panel;
    }

    public void contextChange(ViewContextChangeEvent evt) {
        if (ToolWindowTypeDescriptor.class.equals(evt.getProperty())) {
            if (evt.getNewValue().equals(DockedTypeDescriptor.class)) {
                ToolWindow toolWindow = (ToolWindow) viewContext.get(ToolWindow.class);
                DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
                viewContext.put(DockedTypeDescriptor.class, descriptor);

                popupMenuEnabled.setSelected(descriptor.isPopupMenuEnabled());
                hideLabelOnVisible.setSelected(descriptor.isHideRepresentativeButtonOnVisible());
                idVisibleOnTitleBar.setSelected(descriptor.isIdVisibleOnTitleBar());
                dockLength.setValue(descriptor.getDockLength());

                previewEnabled.setSelected(descriptor.isPreviewEnabled());
                previewDelay.setValue(descriptor.getPreviewDelay());
                previewTransparentRatio.setValue(descriptor.getPreviewTransparentRatio());

                viewContext.put(ToolWindowTypeDescriptor.class, this);
            }
        }
    }


}