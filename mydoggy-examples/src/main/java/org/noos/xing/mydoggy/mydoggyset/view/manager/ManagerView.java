package org.noos.xing.mydoggy.mydoggyset.view.manager;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.PushAwayMode;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowManagerDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.yasaf.plaf.action.ChangeListenerAction;
import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.action.ViewContextSource;
import org.noos.xing.yasaf.plaf.bean.AndSource;
import org.noos.xing.yasaf.plaf.bean.ChecBoxSelectionSource;
import org.noos.xing.yasaf.plaf.bean.ConstantSource;
import org.noos.xing.yasaf.plaf.bean.SpinnerValueSource;
import org.noos.xing.yasaf.plaf.component.MatrixPanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
        panel.setLayout(new TableLayout(new double[][]{{-1}, {120, 3, -1}}));
        panel.add(new ToolWindowManagerDescriptorPrefView(viewContext).getComponent(), "0,0,FULL,FULL");
        panel.add(new PersistencePrefView(viewContext).getComponent(), "0,2,FULL,FULL");

        viewContext.put(ToolWindowManagerDescriptor.class, toolWindowManager.getToolWindowManagerDescriptor());

        return panel;
    }


    public class ToolWindowManagerDescriptorPrefView extends ComponentView implements ViewContextChangeListener {
        private JSpinner leftDividerSize, rightDividerSize, topDividerSize, bottomDividerSize;
        private JCheckBox numberingEnabled;
        private JComboBox pushAwayMode;

        public ToolWindowManagerDescriptorPrefView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            MatrixPanel panel = new MatrixPanel(2, 4);
            panel.setBorder(new TitledBorder("ToolWindowManagerDescriptor Preference"));

            // Right
            panel.addPair(0, 0, "numberingEnabled : ", numberingEnabled = new JCheckBox());
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
            panel.addPair(0, 1, "pushAwayMode : ", pushAwayMode);

            // Left
            panel.addPair(1, 0,
                          "DividerSize (LEFT) : ",
                          leftDividerSize = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)));
            leftDividerSize.addChangeListener(
                    new ChangeListenerAction(ToolWindowManagerDescriptor.class,
                                             "setDividerSize",
                                             new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                             new AndSource(
                                                     new ConstantSource(ToolWindowAnchor.LEFT),
                                                     new SpinnerValueSource(leftDividerSize)
                                             )
                    )
            );

            panel.addPair(1, 1,
                          "DividerSize (RIGHT) : ",
                          rightDividerSize = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)));
            rightDividerSize.addChangeListener(
                    new ChangeListenerAction(ToolWindowManagerDescriptor.class,
                                             "setDividerSize",
                                             new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                             new AndSource(
                                                     new ConstantSource(ToolWindowAnchor.RIGHT),
                                                     new SpinnerValueSource(rightDividerSize)
                                             )
                    )
            );

            panel.addPair(1, 2,
                          "DividerSize (TOP) : ",
                          topDividerSize = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)));
            topDividerSize.addChangeListener(
                    new ChangeListenerAction(ToolWindowManagerDescriptor.class,
                                             "setDividerSize",
                                             new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                             new AndSource(
                                                     new ConstantSource(ToolWindowAnchor.TOP),
                                                     new SpinnerValueSource(topDividerSize)
                                             )
                    )
            );

            panel.addPair(1, 3,
                          "DividerSize (BOTTOM) : ",
                          bottomDividerSize = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)));
            bottomDividerSize.addChangeListener(
                    new ChangeListenerAction(ToolWindowManagerDescriptor.class,
                                             "setDividerSize",
                                             new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                                             new AndSource(
                                                     new ConstantSource(ToolWindowAnchor.BOTTOM),
                                                     new SpinnerValueSource(bottomDividerSize)
                                             )
                    )
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
            viewContext.addViewContextChangeListener(ToolWindowManagerDescriptor.class, this);
        }

        public void contextChange(ViewContextChangeEvent evt) {
            ToolWindowManagerDescriptor managerDescriptor = (ToolWindowManagerDescriptor) evt.getNewValue();

            numberingEnabled.setSelected(managerDescriptor.isNumberingEnabled());
            pushAwayMode.setSelectedItem(managerDescriptor.getPushAwayMode());
            leftDividerSize.setValue(managerDescriptor.getDividerSize(ToolWindowAnchor.LEFT));
            rightDividerSize.setValue(managerDescriptor.getDividerSize(ToolWindowAnchor.RIGHT));
            topDividerSize.setValue(managerDescriptor.getDividerSize(ToolWindowAnchor.TOP));
            bottomDividerSize.setValue(managerDescriptor.getDividerSize(ToolWindowAnchor.BOTTOM));
        }
    }

    public class PersistencePrefView extends ComponentView {
        protected JEditorPane editorPane;

        public PersistencePrefView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JPanel panel = new JPanel(new TableLayout(new double[][]{{3,100,3,-1,3},{3,20,3,20,3,20,3,-1,3}}));
            panel.setBorder(new TitledBorder("Persistence Preference"));

            JButton save = new JButton("Save ->");
            save.addActionListener(new ViewContextAction(viewContext, "save"));
            panel.add(save, "1,1,FULL,FULL");

            JButton load = new JButton("<- Load");
            load.addActionListener(new ViewContextAction(viewContext, "load"));
            panel.add(load, "1,3,FULL,FULL");

            JButton clear = new JButton("Clear");
            clear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    viewContext.put("output", "");
                }
            });
            panel.add(clear, "1,5,FULL,FULL");

            editorPane = new JEditorPane();
            panel.add(new JScrollPane(editorPane), "3,1,3,7");

            return panel;
        }

        protected void initListeners() {
            viewContext.addViewContextChangeListener("save", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    viewContext.get(ToolWindowManager.class).getPersistenceDelegate().save(outputStream);
                    viewContext.put("output", outputStream.toString());
                }
            });
            viewContext.addViewContextChangeListener("load", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(editorPane.getText().getBytes());
                    viewContext.get(ToolWindowManager.class).getPersistenceDelegate().apply(inputStream);
                }
            });
            viewContext.addViewContextChangeListener("output", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    editorPane.setText((String) viewContext.get("output"));
                }
            });
        }

    }

}