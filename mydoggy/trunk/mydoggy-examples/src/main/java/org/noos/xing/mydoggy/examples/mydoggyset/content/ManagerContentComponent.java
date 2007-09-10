package org.noos.xing.mydoggy.examples.mydoggyset.content;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.DesktopContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.examples.mydoggyset.view.contents.model.ContentsTableModel;
import org.noos.xing.mydoggy.examples.mydoggyset.ui.CheckBoxCellRenderer;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyDesktopContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyTabbedContentManagerUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.util.Map;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ManagerContentComponent extends JPanel {
    private ToolWindowManager toolWindowManager;
    private Map<Class, ContentManagerUI> map;

    private JPanel contentManagerUIPrefPanel;
    private JPanel tabbedManagerUIPrefPanel;
    private JPanel dektopManagerUIPrefPanel;

    private boolean valueChanging = false;

    public ManagerContentComponent(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        this.map = new Hashtable<Class, ContentManagerUI>();
        initComponents();
    }

    protected void initComponents() {
        // Preference Panel
        JPanel prefPanel = new JPanel(new TableLayout(new double[][]{{-1},{20,3,-1}}));
        prefPanel.setBorder(new TitledBorder("Preferences"));

        prefPanel.add(contentManagerUIPrefPanel = initContentUIManagerPrefPanel(), "0,2,FULL,FULL");
        prefPanel.add(initContentUIManagerSelectorPanel(), "0,0,FULL,FULL");

        // TODO: add changing ui...

        // Contents Panel

        JPanel contensPanel = new JPanel(new TableLayout(new double[][]{{-1},{-1}}));
        contensPanel.setBorder(new TitledBorder("Contents"));

        JTable contentsTable = new JTable(new ContentsTableModel(toolWindowManager));
        contentsTable.getTableHeader().setReorderingAllowed(false);

        JCheckBox booleanEditor = new JCheckBox();
        booleanEditor.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 4; i++) {
            contentsTable.getColumnModel().getColumn(i).setCellRenderer(new CheckBoxCellRenderer());
            contentsTable.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(booleanEditor));
        }

        contensPanel.add(new JScrollPane(contentsTable), "0,0,FULL,FULL");

        // Setup main panel
        setLayout(new TableLayout(new double[][]{{-1},{-1,5,-1}}));
        add(prefPanel, "0,0,FULL,FULL");
        add(contensPanel, "0,2,FULL,FULL");
    }

    protected JPanel initContentUIManagerSelectorPanel() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{150,3,-1},{-1}}));

        JComboBox uis = new JComboBox(new Object[]{
                TabbedContentManagerUI.class,
                DesktopContentManagerUI.class
        });

        ContentManagerUI managerUI = toolWindowManager.getContentManager().getContentManagerUI();
        if (managerUI instanceof TabbedContentManagerUI) {
            uis.setSelectedIndex(0);
            map.put(TabbedContentManagerUI.class, managerUI);
            map.put(DesktopContentManagerUI.class, new MyDoggyDesktopContentManagerUI());
        } else {
            uis.setSelectedIndex(1);
            map.put(DesktopContentManagerUI.class, managerUI);
            map.put(TabbedContentManagerUI.class, new MyDoggyTabbedContentManagerUI());
        }

        tabbedManagerUIPrefPanel = initTabbedManagerUIPrefPanel();
        dektopManagerUIPrefPanel = initDesktopManagerUIPrefPanel();

        if (managerUI instanceof TabbedContentManagerUI) {
            setContentManagerUIPrefPanel(tabbedManagerUIPrefPanel);
        } else {
            setContentManagerUIPrefPanel(dektopManagerUIPrefPanel);
        }

        uis.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getItem() instanceof TabbedContentManagerUI)
                    setContentManagerUIPrefPanel(tabbedManagerUIPrefPanel);
                else
                    setContentManagerUIPrefPanel(dektopManagerUIPrefPanel);

                toolWindowManager.getContentManager().setContentManagerUI(map.get(e.getItem()));
            }
        });

        panel.add(new JLabel("ContentManagerUI : "), "0,0,r,FULL");
        panel.add(uis, "2,0,FULL,FULL");

        return panel;
    }

    protected JPanel initTabbedManagerUIPrefPanel() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{3, 120, 3, -1, 3},{3, 20, 3, 20, 3, 20, 3}}));

        // Tab Placement
        panel.add(new JLabel("Tab Placement : "), "1,1,r,c");
        final JComboBox tabPlaces = new JComboBox(new Object[]{
                TabbedContentManagerUI.TabPlacement.TOP,
                TabbedContentManagerUI.TabPlacement.LEFT,
                TabbedContentManagerUI.TabPlacement.BOTTOM,
                TabbedContentManagerUI.TabPlacement.RIGHT
        });
        tabPlaces.setSelectedIndex(
                ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).getTabPlacement().ordinal()
        );
        tabPlaces.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    valueChanging = true;
                    ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).setTabPlacement(
                            (TabbedContentManagerUI.TabPlacement) tabPlaces.getSelectedItem()
                    );
                    valueChanging = false;
                }
            }
        });
        panel.add(tabPlaces, "3,1,FULL,FULL");

        // Tab Layout
        panel.add(new JLabel("Tab Layout : "), "1,3,r,c");
        final JComboBox tabLayouts = new JComboBox(new Object[]{
                TabbedContentManagerUI.TabLayout.SCROLL,
                TabbedContentManagerUI.TabLayout.WRAP
        });
        tabLayouts.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    valueChanging = true;
                    ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).setTabLayout(
                            (TabbedContentManagerUI.TabLayout) tabLayouts.getSelectedItem()
                    );
                    valueChanging = false;
                }
            }
        });
        tabLayouts.setSelectedIndex(
                ((TabbedContentManagerUI) toolWindowManager.getContentManager().getContentManagerUI()).getTabLayout().ordinal()
        );
        panel.add(tabLayouts, "3,3,FULL,FULL");

        ((TabbedContentManagerUI) map.get(TabbedContentManagerUI.class)).addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (valueChanging)
                    return;

                String pName = evt.getPropertyName();
                if ("tabPlacement".equals(pName)) {
                    tabPlaces.setSelectedIndex(((TabbedContentManagerUI.TabPlacement)evt.getNewValue()).ordinal());
                } else if ("tabLayout".equals(pName)) {
                    tabLayouts.setSelectedIndex(((TabbedContentManagerUI.TabLayout)evt.getNewValue()).ordinal());
                }
            }
        });

        return panel;
    }

    protected JPanel initDesktopManagerUIPrefPanel() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{-1, 50, -1},{-1, 20, -1}}));
        panel.add(new JLabel("No Pref"), "1,1,FULL,FULL");

        return panel;
    }

    protected JPanel initContentUIManagerPrefPanel() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{-1},{-1}}));
        panel.setBorder(new LineBorder(Color.DARK_GRAY));

        return panel;
    }

    protected void setContentManagerUIPrefPanel(Component component) {
        contentManagerUIPrefPanel.removeAll();
        contentManagerUIPrefPanel.add(component, "0,0,FULL,FULL");

        contentManagerUIPrefPanel.revalidate();
        contentManagerUIPrefPanel.repaint();
    }

}