package org.noos.xing.mydoggy.mydoggyset.view.manager;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.action.ChangeListenerAction;
import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.action.ViewContextSource;
import org.noos.xing.yasaf.plaf.bean.ChecBoxSelectionSource;
import org.noos.xing.yasaf.plaf.bean.SpinnerValueSource;
import org.noos.xing.yasaf.plaf.component.MatrixPanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.plaf.view.listener.ContextPutItemListener;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ManagerView implements View {
    protected ToolWindowManager toolWindowManager;

    protected enum ToolBarKey {
        LEFT_TOOLBAR,
        RIGHT_TOOLBAR,
        TOP_TOOLBAR,
        BOTTOM_TOOLBAR
    }


    public ManagerView(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }


    public Component getComponent() {
        ViewContext viewContext = new MapViewContext();
        viewContext.put(ToolWindowManager.class, toolWindowManager);
        viewContext.put(ToolBarKey.BOTTOM_TOOLBAR, toolWindowManager.getToolWindowBar(ToolWindowAnchor.BOTTOM));
        viewContext.put(ToolBarKey.LEFT_TOOLBAR, toolWindowManager.getToolWindowBar(ToolWindowAnchor.LEFT));
        viewContext.put(ToolBarKey.RIGHT_TOOLBAR, toolWindowManager.getToolWindowBar(ToolWindowAnchor.RIGHT));
        viewContext.put(ToolBarKey.TOP_TOOLBAR, toolWindowManager.getToolWindowBar(ToolWindowAnchor.TOP));

        JPanel panel = new JPanel();
        panel.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {100, 3, 150, 3, -1}}));
        panel.add(new ToolWindowManagerDescriptorView(viewContext).getComponent(), "0,0,FULL,FULL");
        panel.add(new ToolWindowBarsView(toolWindowManager).getComponent(), "0,2,FULL,FULL");
        panel.add(new PersistenceDelegateView(viewContext).getComponent(), "0,4,FULL,FULL");

        viewContext.put(ToolWindowManagerDescriptor.class, toolWindowManager.getToolWindowManagerDescriptor());

        return panel;
    }


    public class ToolWindowManagerDescriptorView extends ComponentView implements ViewContextChangeListener {
        private JCheckBox numberingEnabled, previewEnabled, showUnavailableTools;
        private JComboBox pushAwayMode;


        public ToolWindowManagerDescriptorView(ViewContext viewContext) {
            super(viewContext);
        }


        protected Component initComponent() {
            MatrixPanel panel = new MatrixPanel(2, 2);
            panel.setBorder(new TitledBorder("ToolWindowManager#getToolWindowManagerDescriptor()"));

            // Column 0
            panel.addEntry(0, 0, "numberingEnabled : ", numberingEnabled = new JCheckBox());
            numberingEnabled.setAction(new DynamicAction(ToolWindowManagerDescriptor.class,
                    "numberingEnabled",
                    new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                    new ChecBoxSelectionSource(numberingEnabled)));

            panel.addEntry(1, 0, "previewEnabled : ", previewEnabled = new JCheckBox());
            previewEnabled.setAction(new DynamicAction(ToolWindowManagerDescriptor.class,
                    "previewEnabled",
                    new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                    new ChecBoxSelectionSource(previewEnabled)));


            // Column 1

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
            panel.addEntry(0, 1, "pushAwayMode : ", pushAwayMode);

            panel.addEntry(1, 1, "showUnavailableTools : ", showUnavailableTools = new JCheckBox());
            showUnavailableTools.setAction(new DynamicAction(ToolWindowManagerDescriptor.class,
                    "showUnavailableTools",
                    new ViewContextSource(viewContext, ToolWindowManagerDescriptor.class),
                    new ChecBoxSelectionSource(showUnavailableTools)));


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
            previewEnabled.setSelected(managerDescriptor.isPreviewEnabled());
            showUnavailableTools.setSelected(managerDescriptor.isShowUnavailableTools());
            pushAwayMode.setSelectedItem(managerDescriptor.getPushAwayMode());
        }
    }

    public class ToolWindowBarsView extends ComponentView {
        protected JPanel mainPanel;

        public ToolWindowBarsView(ToolWindowManager toolWindowManager) {
            super();
            viewContext.put(ToolWindowManager.class, toolWindowManager);
        }


        protected Component initComponent() {
            mainPanel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {20, 3, -1}}));
            mainPanel.setBorder(new TitledBorder("ToolWindowManager#getToolWindowBar(ToolWindowAnchor)"));

            mainPanel.add(new ToolWindowBarView(viewContext).getComponent(), "0,2,FULL,FULL");
            mainPanel.add(new ToolWindowAnchorSelectorView(viewContext).getComponent(), "0,0,FULL,FULL");

            return mainPanel;
        }


        protected class ToolWindowBarView extends ComponentView implements ViewContextChangeListener {
            protected ToolWindowBar toolWindowBar;

            protected JCheckBox visible, aggregateMode;
            private JSpinner dividerSize, length;


            public ToolWindowBarView(ViewContext viewContext) {
                super(viewContext);
            }

            protected Component initComponent() {
                MatrixPanel panel = new MatrixPanel(4, 2);
                panel.setBorder(new TitledBorder("Properties"));

                // Left
                panel.addEntry(0, 0, "visible : ", visible = new JCheckBox());
                visible.setAction(new DynamicAction(ToolWindowBar.class,
                        "visible",
                        new ViewContextSource(viewContext, ToolWindowBar.class),
                        new ChecBoxSelectionSource(visible)));

                panel.addEntry(1, 0, "aggregateMode : ", aggregateMode = new JCheckBox());
                aggregateMode.setAction(new DynamicAction(ToolWindowBar.class,
                        "aggregateMode",
                        new ViewContextSource(viewContext, ToolWindowBar.class),
                        new ChecBoxSelectionSource(aggregateMode)));

                // Right

                panel.addEntry(0, 1, "dividerSize : ", dividerSize = new JSpinner(new SpinnerNumberModel(5, 0, 50, 1)));
                dividerSize.addChangeListener(new ChangeListenerAction(ToolWindowBar.class,
                        "dividerSize",
                        new ViewContextSource(viewContext, ToolWindowBar.class),
                        new SpinnerValueSource(dividerSize)
                )
                );

                panel.addEntry(1, 1, "length : ", length = new JSpinner(new SpinnerNumberModel(5, 0, 50, 1)));
                length.addChangeListener(new ChangeListenerAction(ToolWindowBar.class,
                        "length",
                        new ViewContextSource(viewContext, ToolWindowBar.class),
                        new SpinnerValueSource(length)
                )
                );

                return panel;
            }

            protected void initListeners() {
                viewContext.addViewContextChangeListener(ToolWindowAnchor.class, this);
            }

            public void contextChange(ViewContextChangeEvent evt) {
                if (ToolWindowAnchor.class.equals(evt.getProperty())) {
                    toolWindowBar = viewContext.get(ToolWindowManager.class).getToolWindowBar((ToolWindowAnchor) evt.getNewValue());
                    viewContext.put(ToolWindowBar.class, toolWindowBar);

                    visible.setSelected(toolWindowBar.isVisible());
                    aggregateMode.setSelected(toolWindowBar.isAggregateMode());
                    dividerSize.setValue(toolWindowBar.getDividerSize());
                    length.setValue(toolWindowBar.getLength());
                }
            }

        }

        protected class ToolWindowAnchorSelectorView extends ComponentView {
            protected JComboBox anchors;


            public ToolWindowAnchorSelectorView(ViewContext viewContext) {
                super(viewContext);
            }


            protected Component initComponent() {
                JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{3, 150, 3, -1, 3}, {-1}}));

                anchors = new JComboBox(new Object[]{
                        ToolWindowAnchor.LEFT,
                        ToolWindowAnchor.RIGHT,
                        ToolWindowAnchor.TOP,
                        ToolWindowAnchor.BOTTOM,
                });
                anchors.addItemListener(new ContextPutItemListener(viewContext, ToolWindowAnchor.class));

                panel.add(new JLabel("ToolWindowAnchor : "), "1,0,r,FULL");
                panel.add(anchors, "3,0,FULL,FULL");

                return panel;
            }

            @Override
            protected void onFirstVisible() {
                anchors.setSelectedIndex(1);
                anchors.setSelectedIndex(0);
            }
        }
    }

    public class PersistenceDelegateView extends ComponentView {
        protected JEditorPane editorPane;


        public PersistenceDelegateView(ViewContext viewContext) {
            super(viewContext);
        }


        protected Component initComponent() {
            JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{3, 100, 3, -1, 3}, {3, 20, 3, 20, 3, 20, 3, -1, 3}}));
            panel.setBorder(new TitledBorder("ToolWindowManager#getPersistenceDelegate()"));

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