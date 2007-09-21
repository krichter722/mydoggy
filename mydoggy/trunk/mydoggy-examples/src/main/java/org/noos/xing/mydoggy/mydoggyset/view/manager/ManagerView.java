package org.noos.xing.mydoggy.mydoggyset.view.manager;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.yasaf.plaf.action.ChangeListenerAction;
import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.plaf.action.ViewContextSource;
import org.noos.xing.yasaf.plaf.bean.ChecBoxSelectionSource;
import org.noos.xing.yasaf.plaf.bean.SpinnerValueSource;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ManagerView implements View {
    protected ToolWindowManager toolWindowManager;

    public ManagerView(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public Component getComponent() {
        ViewContext viewContext = new MapViewContext();
        viewContext.put(ToolWindowManager.class, toolWindowManager);

        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(new double[][]{{-1}, {-1}}));
        panel.add(new ToolWindowManagerDescriptorPrefView(viewContext).getComponent(), "0,0,FULL,FULL");

        return panel;
    }

    public class ToolWindowManagerDescriptorPrefView extends ComponentView {
        private JSpinner leftDividerSize, rightDivederSize, topDividerSize, bottomDividerSize;
        private JCheckBox numberingEnabled;
        private JComboBox pushAwayMode;

        public ToolWindowManagerDescriptorPrefView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            ToolWindowManagerDescriptor managerDescriptor = viewContext.get(ToolWindowManager.class).getToolWindowManagerDescriptor();
            viewContext.put(ToolWindowManagerDescriptor.class, managerDescriptor);

            JPanel panel = new JPanel(new TableLayout(new double[][]{{3, -2, 3, -1, 3, -2, 3, -1, 3}, {-1, 20, 3, 20, 3, 20, 3, 20, -1}}));
            panel.setBorder(new TitledBorder("ToolWindowManagerDescriptor Preference"));

            // Right
            panel.add(new JLabel("numberingEnabled : "), "1,1,r,c");
            panel.add(numberingEnabled = new JCheckBox(), "3,1,FULL,FULL");
            numberingEnabled.setAction(new DynamicAction(ToolWindowManagerDescriptor.class,
                                                       "numberingEnabled",
                                                       new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                                       new ChecBoxSelectionSource(numberingEnabled)));

            panel.add(new JLabel("pushAwayMode : "), "1,3,r,c");
            pushAwayMode = new JComboBox(new Object[]{
                    PushAwayMode.ANTICLOCKWISE,
                    PushAwayMode.HORIZONTAL,
                    PushAwayMode.VERTICAL,
                    PushAwayMode.MOST_RECENT
            });
            pushAwayMode.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    viewContext.put(PushAwayMode.class, e.getItem());
                }
            });
            panel.add(pushAwayMode, "3,3,FULL,FULL");

            // Left
            panel.add(new JLabel("DividerSize (LEFT) : "), "5,1,r,c");
            panel.add(leftDividerSize = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), "7,1,FULL,FULL");
            leftDividerSize.addChangeListener(
                    new ChangeListenerAction(ToolWindowManagerDescriptor.class,
                                             "dividerSize",
                                             new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                             new SpinnerValueSource(leftDividerSize))
            );

            panel.add(new JLabel("DividerSize (RIGHT) : "), "5,3,r,c");
            panel.add(rightDivederSize = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), "7,3,FULL,FULL");
            rightDivederSize.addChangeListener(
                    new ChangeListenerAction(ToolWindowManagerDescriptor.class,
                                             "dividerSize",
                                             new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                             new SpinnerValueSource(rightDivederSize))
            );

            panel.add(new JLabel("DividerSize (TOP) : "), "5,5,r,c");
            panel.add(topDividerSize = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), "7,5,FULL,FULL");
            topDividerSize.addChangeListener(
                    new ChangeListenerAction(ToolWindowManagerDescriptor.class,
                                             "dividerSize",
                                             new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                             new SpinnerValueSource(topDividerSize))
            );

            panel.add(new JLabel("DividerSize (BOTTOM) : "), "5,7,r,c");
            panel.add(bottomDividerSize = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), "7,7,FULL,FULL");
            bottomDividerSize.addChangeListener(
                    new ChangeListenerAction(ToolWindowManagerDescriptor.class,
                                             "dividerSize",
                                             new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                             new SpinnerValueSource(bottomDividerSize))
            );

            return panel;
        }

        protected void initListeners() {
            viewContext.addViewContextChangeListener(PushAwayMode.class, new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    ToolWindowManagerDescriptor managerDescriptor = viewContext.get(ToolWindowManagerDescriptor.class);
                    managerDescriptor.setPushAwayMode((PushAwayMode) evt.getNewValue());
                }
            });
        }
    }

}