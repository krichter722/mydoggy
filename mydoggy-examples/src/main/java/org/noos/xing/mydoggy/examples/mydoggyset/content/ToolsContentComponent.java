package org.noos.xing.mydoggy.examples.mydoggyset.content;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.examples.mydoggyset.action.ChecBoxSelectionSource;
import org.noos.xing.mydoggy.examples.mydoggyset.action.DynamicAction;
import org.noos.xing.mydoggy.examples.mydoggyset.model.ToolsTableModel;
import org.noos.xing.mydoggy.examples.mydoggyset.signal.SignalListener;
import org.noos.xing.mydoggy.examples.mydoggyset.signal.SignalManager;
import org.noos.xing.mydoggy.examples.mydoggyset.signal.SignalEvent;
import org.noos.xing.mydoggy.examples.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsContentComponent extends JPanel {
    private ToolWindowManager toolWindowManager;
    private JPanel typeDescriptroPrefPanel;

    JComboBox types;

    public ToolsContentComponent(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        initComponents();
    }

    protected void initComponents() {
        // Tools Panel

        JPanel toolsPanel = new JPanel(new TableLayout(new double[][]{{-1}, {-1}}));
        toolsPanel.setBorder(new TitledBorder("ToolWindows"));

        final JTable toolsTable = new JTable(new ToolsTableModel(toolWindowManager));
        toolsTable.getTableHeader().setReorderingAllowed(false);
        toolsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (toolsTable.getSelectedRow() != -1) {
                    ToolWindow selectedTool = (ToolWindow) toolsTable.getModel().getValueAt(toolsTable.getSelectedRow(), -1);
                    SignalManager.getInstance().sendEvent(ToolWindow.class, selectedTool);

                    types.setSelectedIndex(0);
                }
            }
        });

        // Type column
        JComboBox types = new JComboBox(new Object[]{ToolWindowType.DOCKED,
                                                     ToolWindowType.SLIDING, ToolWindowType.FLOATING, ToolWindowType.FLOATING_FREE});
        toolsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(types));

        // Anchor column
        JComboBox anchors = new JComboBox(new Object[]{ToolWindowAnchor.LEFT,
                                                       ToolWindowAnchor.RIGHT, ToolWindowAnchor.BOTTOM, ToolWindowAnchor.TOP});
        toolsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(anchors));

        // Available, Visible, Active columns
        JCheckBox booleanEditor = new JCheckBox();
        booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);
        toolsTable.getColumnModel().getColumn(4).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(booleanEditor));

        toolsTable.getColumnModel().getColumn(5).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(booleanEditor));

        toolsTable.getColumnModel().getColumn(6).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(booleanEditor));

        toolsTable.getColumnModel().getColumn(8).setCellRenderer(new CheckBoxCellRenderer());
        toolsTable.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(booleanEditor));

        // Index column
        TableColumn indexColumn = toolsTable.getColumnModel().getColumn(7);
        indexColumn.setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Integer index = (Integer) value;
                if (index == -1)
                    setText("No Index");
                return c;
            }
        });
        JComboBox indexs = new JComboBox(new Object[]{-1, 1, 2, 3, 4, 5, 6, 7, 8, 9});
        indexs.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Integer toolIndex = (Integer) value;
                if (toolIndex == -1)
                    setText("No Index");
                return c;
            }
        });
        indexColumn.setCellEditor(new DefaultCellEditor(indexs));

        toolsPanel.setLayout(new TableLayout(new double[][]{{-1}, {-1}}));
        toolsPanel.add(new JScrollPane(toolsTable), "0,0,FULL,FULL");

        // Preference Panel

        JPanel prefPanel = new JPanel(new TableLayout(new double[][]{{-1}, {20, 3, -1}}));
        prefPanel.setBorder(new TitledBorder("Preferences"));

        prefPanel.add(typeDescriptroPrefPanel = initTypeDescriptorPrefPanel(), "0,2,FULL,FULL");
        prefPanel.add(initTypeDescriptorSelectorPanel(), "0,0,FULL,FULL");

        // Setup main panel
        setLayout(new TableLayout(new double[][]{{-1}, {-1, 5, -1}}));
        add(toolsPanel, "0,0,FULL,FULL");
        add(prefPanel, "0,2,FULL,FULL");
    }

    protected JPanel initTypeDescriptorSelectorPanel() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{150, 3, -1}, {-1}}));

        types = new JComboBox(new Object[]{
                DockedTypeDescriptor.class,
                SlidingTypeDescriptor.class,
                FloatingTypeDescriptor.class
        });

        final Component dock = initDockedTypeDescrPrefPanel();
        final Component sliding = initSlidingTypeDescrPrefPanel();
        final Component floating = initFloatingTypeDescrPrefPanel();

        types.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getItem().equals(DockedTypeDescriptor.class))
                    setTypeDescriptorPrefPanel(dock);
                else if (e.getItem().equals(SlidingTypeDescriptor.class))
                    setTypeDescriptorPrefPanel(sliding);
                else if (e.getItem().equals(FloatingTypeDescriptor.class))
                    setTypeDescriptorPrefPanel(floating);
            }
        });
        SignalManager.getInstance().addSignalListener(ToolWindow.class, new SignalListener() {
            public void handleSignalEvent(String signal, SignalEvent event) {
                setTypeDescriptorPrefPanel(dock);
            }
        });

        panel.add(new JLabel("Type Descriptor : "), "0,0,r,FULL");
        panel.add(types, "2,0,FULL,FULL");

        return panel;
    }

    protected JPanel initTypeDescriptorPrefPanel() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{-1}, {-1}}));
        panel.setBorder(new LineBorder(Color.DARK_GRAY));

        return panel;
    }

    protected void setTypeDescriptorPrefPanel(Component component) {
        typeDescriptroPrefPanel.removeAll();
        typeDescriptroPrefPanel.add(component, "0,0,FULL,FULL");

        SwingUtil.repaint(typeDescriptroPrefPanel);
//        typeDescriptroPrefPanel.revalidate();
//        typeDescriptroPrefPanel.repaint();
    }

    protected JPanel initDockedTypeDescrPrefPanel() {
        return new DockedTypeDescrPrefPanel();
    }

    protected JPanel initSlidingTypeDescrPrefPanel() {
        return new SlidingTypeDescrPrefPanel();
    }

    protected JPanel initFloatingTypeDescrPrefPanel() {
        return new FloatingTypeDescrPrefPanel();
    }

    private class DockedTypeDescrPrefPanel extends JPanel implements SignalListener<ToolWindow> {
        private JCheckBox popupMenuEnabled, hideLabelOnVisible, idVisibleOnToolBar, previewEnabled;
        private JTextField dockLength, previewDelay, previewTransparentRatio;

        private DockedTypeDescrPrefPanel() {
            super(new TableLayout(new double[][]{{3, -2, 3, -1, 3, -2, 3, -1, 3}, {-1, 20, 3, 20, 3, 20, 3, 20, -1}}));

            SignalManager.getInstance().addSignalListener(ToolWindow.class, this);

            // Left
            add(new JLabel("PopupMenuEnabled : "), "1,1,r,c");
            add(popupMenuEnabled = new JCheckBox(), "3,1,FULL,FULL");
            popupMenuEnabled.setAction(new DynamicAction(DockedTypeDescriptor.class,
                                                         "popupMenuEnabled",
                                                         new ChecBoxSelectionSource(popupMenuEnabled)));

            add(new JLabel("HideLabelOnVisible : "), "1,3,r,c");
            add(hideLabelOnVisible = new JCheckBox(), "3,3,FULL,FULL");
            hideLabelOnVisible.setAction(new DynamicAction(DockedTypeDescriptor.class,
                                                           "hideLabelOnVisible",
                                                           new ChecBoxSelectionSource(hideLabelOnVisible)));

            add(new JLabel("IdVisibleOnToolBar : "), "1,5,r,c");
            add(idVisibleOnToolBar = new JCheckBox(), "3,5,FULL,FULL");
            idVisibleOnToolBar.setAction(new DynamicAction(DockedTypeDescriptor.class,
                                                           "idVisibleOnToolBar",
                                                           new ChecBoxSelectionSource(idVisibleOnToolBar)));

            add(new JLabel("dockLength : "), "1,7,r,c");
            add(dockLength = new JTextField(), "3,7,FULL,FULL");

            // Right
            add(new JLabel("previewEnabled : "), "5,1,r,c");
            add(previewEnabled = new JCheckBox(), "7,1,FULL,FULL");
            previewEnabled.setAction(new DynamicAction(DockedTypeDescriptor.class,
                                                       "previewEnabled",
                                                       new ChecBoxSelectionSource(previewEnabled)));

            add(new JLabel("previewDelay : "), "5,3,r,c");
            add(previewDelay = new JTextField(), "7,3,FULL,FULL");

            add(new JLabel("previewTransparentRatio : "), "5,5,r,c");
            add(previewTransparentRatio = new JTextField(), "7,5,FULL,FULL");
        }

        public void handleSignalEvent(String signal, SignalEvent<ToolWindow> event) {
            DockedTypeDescriptor descriptor = (DockedTypeDescriptor) event.getMessage().getTypeDescriptor(ToolWindowType.DOCKED);

            SignalManager.getInstance().sendEvent(DockedTypeDescriptor.class, descriptor);

            popupMenuEnabled.setSelected(descriptor.isPopupMenuEnabled());
            hideLabelOnVisible.setSelected(descriptor.isHideLabelOnVisible());
            idVisibleOnToolBar.setSelected(descriptor.isIdVisibleOnToolBar());
            dockLength.setText(String.valueOf(descriptor.getDockLength()));

            previewEnabled.setSelected(descriptor.isPreviewEnabled());
            previewDelay.setText(String.valueOf(descriptor.getPreviewDelay()));
            previewTransparentRatio.setText(String.valueOf(descriptor.getPreviewTransparentRatio()));
        }

    }

    private class SlidingTypeDescrPrefPanel extends JPanel implements SignalListener<ToolWindow> {
        private JCheckBox enabled, transparentMode;
        private JTextField transparentRatio, transparentDelay;

        private SlidingTypeDescrPrefPanel() {
            super(new TableLayout(new double[][]{{3, -2, 3, -1, 3}, {-1, 20, 3, 20, 3, 20, 3, 20, -1}}));

            SignalManager.getInstance().addSignalListener(ToolWindow.class, this);

            add(new JLabel("enabled : "), "1,1,r,c");
            add(enabled = new JCheckBox(), "3,1,FULL,FULL");
            enabled.setAction(new DynamicAction(SlidingTypeDescriptor.class,
                                                "enabled",
                                                new ChecBoxSelectionSource(enabled)));

            add(new JLabel("transparentMode : "), "1,3,r,c");
            add(transparentMode = new JCheckBox(), "3,3,FULL,FULL");
            transparentMode.setAction(new DynamicAction(SlidingTypeDescriptor.class,
                                                "transparentMode",
                                                new ChecBoxSelectionSource(transparentMode)));

            add(new JLabel("transparentRatio : "), "1,5,r,c");
            add(transparentRatio = new JTextField(), "3,5,FULL,FULL");

            add(new JLabel("transparentDelay : "), "1,7,r,c");
            add(transparentDelay = new JTextField(), "3,7,FULL,FULL");
        }

        public void handleSignalEvent(String signal, SignalEvent<ToolWindow> event) {
            SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) event.getMessage().getTypeDescriptor(ToolWindowType.SLIDING);
            SignalManager.getInstance().sendEvent(SlidingTypeDescriptor.class, descriptor);

            enabled.setSelected(descriptor.isEnabled());

            transparentMode.setSelected(descriptor.isTransparentMode());
            transparentDelay.setText(String.valueOf(descriptor.getTransparentDelay()));
            transparentRatio.setText(String.valueOf(descriptor.getTransparentRatio()));
        }

    }

    private class FloatingTypeDescrPrefPanel extends JPanel implements SignalListener<ToolWindow> {

        private FloatingTypeDescrPrefPanel() {
            super(new TableLayout(new double[][]{{3, 120, -1, 3}, {-1, 20, -1}}));
            add(new JLabel("No Pref"), "1,1,FULL,FULL");
        }

        public void handleSignalEvent(String signal, SignalEvent<ToolWindow> event) {

        }
    }

}
