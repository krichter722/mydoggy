package org.noos.xing.mydoggy.scenarioset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.scenarioset.scenario.*;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.listener.ContextListDoubleClickMouseListener;
import org.noos.xing.yasaf.plaf.view.listener.ContextPutListSelectionListener;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ScenarioSet {

    protected JFrame frame;
    protected List<Scenario> scenarios;


    protected void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }


    protected void setUp() {
        initScenarios();
        initComponents();
    }

    protected void initScenarios() {
        scenarios = new ArrayList<Scenario>();

        scenarios.add(new OnForBarScenario());
        scenarios.add(new SaveRestoreContentsScenario());
        scenarios.add(new ToolWindowActionScenario());
        scenarios.add(new AllUnpinnedScenario());
        scenarios.add(new RepresentativeAnchorScenario());
        scenarios.add(new LotOfContentScenario());
        scenarios.add(new ForeignIdScenario());
        scenarios.add(new InfiniteLoopFocusScenario());
    }

    protected void start() {
        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("ScenarioSet...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a simple JMenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        this.frame.setJMenuBar(menuBar);

        // Set a layout manager. I love TableLayout. It's powerful.
        this.frame.getContentPane().setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
        this.frame.add(new ListScenarioView().getComponent(), "1,1,FULL,FULL");

    }


    public static void main(String[] args) {
        ScenarioSet scenarioSet = new ScenarioSet();
        try {
            scenarioSet.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected class ListScenarioView extends ComponentView {
        protected JList scenariosList;

        protected Component initComponent() {
            JPanel mainPanel = new JPanel(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 1, -1, 0}}));


            // Scenarios Panel
            JPanel scenariosPanel = new JPanel(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
            scenariosPanel.setBorder(new TitledBorder("Scenarios"));

            scenariosList = new JList(new ListScenarioListModel());
            scenariosList.addListSelectionListener(new ContextPutListSelectionListener(viewContext, "Scenario", scenariosList));
            scenariosList.addMouseListener(new ContextListDoubleClickMouseListener(viewContext, "Execute"));
            scenariosPanel.add(new JScrollPane(scenariosList), "1,1,FULL,FULL");

            // Descriptions Panel
            JPanel descriptionsPanel = new JPanel(new TableLayout(new double[][]{{0, -1, 120, 0}, {0, -1, 1, 20, 0}}));
            descriptionsPanel.setBorder(new TitledBorder("Description"));
            descriptionsPanel.add(new JButton(new ViewContextAction("Execute", null, viewContext, "Execute", "Scenario")), "2,3,FULL,FULL");

            mainPanel.add(scenariosPanel, "1,1,FULL,FULL");
            mainPanel.add(descriptionsPanel, "1,3,FULL,FULL");

            return mainPanel;
        }


        @Override
        protected void initListeners() {
            viewContext.addViewContextChangeListener("Execute", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    ((Scenario) viewContext.get("Scenario")).launch();
                }
            });
        }

        protected class ListScenarioListModel extends DefaultListModel {

            public ListScenarioListModel() {
                for (Scenario scenario : scenarios) {
                    addElement(scenario);
                }
            }


        }

    }

}
