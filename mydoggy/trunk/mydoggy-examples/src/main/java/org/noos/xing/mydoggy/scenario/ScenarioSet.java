package org.noos.xing.mydoggy.scenario;

import info.clearthought.layout.TableLayout;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ScenarioSet {

    protected JFrame frame;
    protected List<Scenario> scenarios;


    public ScenarioSet() {
        scenarios = new ArrayList<Scenario>();
    }


    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }

    public JFrame getFrame() {
        return frame;
    }

    public void addScenario(Scenario scenario) {
        scenarios.add(scenario);
    }


    protected void setUp() {
        initComponents();
    }

    protected void start() {
        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame(getFrameTitle());
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

    protected String getFrameTitle() {
        return "ScenarioSet...";
    }

    protected String getScenariosPanelBorderTitle() {
        return "Scenarios";
    }



    protected class ListScenarioView extends ComponentView {
        protected JList scenariosList;
        protected JTextArea descriptionArea;
        protected JTextArea sourceArea;

        protected Component initComponent() {
            JPanel mainPanel = new JPanel(new TableLayout(new double[][]{{0, 250, 1, -1, 0}, {0, -1, 1, -1, 0}}));

            // Scenarios Panel
            JPanel scenariosPanel = new JPanel(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
            scenariosPanel.setBorder(new TitledBorder(getScenariosPanelBorderTitle()));

            scenariosList = new JList(new ListScenarioListModel());
            scenariosList.addListSelectionListener(new ContextPutListSelectionListener(viewContext, "Scenario", scenariosList));
            scenariosList.addMouseListener(new ContextListDoubleClickMouseListener(viewContext, "Execute"));
            scenariosPanel.add(new JScrollPane(scenariosList), "1,1,FULL,FULL");

            // Descriptions Panel
            JPanel descriptionsPanel = new JPanel(new TableLayout(new double[][]{{0, -1, 120, 0}, {0, -1, 1, 20, 0}}));
            descriptionsPanel.setBorder(new TitledBorder("Description"));
            descriptionsPanel.add(new JScrollPane(descriptionArea = new JTextArea()), "1,1,2,1");
            descriptionArea.setWrapStyleWord(true);
            descriptionsPanel.add(new JButton(new ViewContextAction("Execute", null, viewContext, "Execute", "Scenario")), "2,3,FULL,FULL");

            // Source Panel
            JPanel sourcePanel = new JPanel(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
            sourcePanel.setBorder(new TitledBorder("Source"));
            sourcePanel.add(new JScrollPane(sourceArea = new JTextArea()), "1,1,FULL,FULL");
            sourceArea.setWrapStyleWord(true);


            mainPanel.add(scenariosPanel, "1,1,FULL,FULL");
            mainPanel.add(descriptionsPanel, "1,3,FULL,FULL");
            mainPanel.add(sourcePanel, "3,1,3,3");

            return mainPanel;
        }


        @Override
        protected void initListeners() {
            viewContext.addViewContextChangeListener("Execute", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    frame.setState(Frame.ICONIFIED);

                    final Window scenarioWindow = ((Scenario) viewContext.get("Scenario")).launch();
                    if (scenarioWindow != null)
                        scenarioWindow .addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                scenarioWindow.removeWindowListener(this);

                                frame.setState(Frame.NORMAL);
                                frame.toFront();
                            }
                        });
                }
            });
            viewContext.addViewContextChangeListener("Scenario", new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    descriptionArea.setText(((Scenario) viewContext.get("Scenario")).getDescription());
                    sourceArea.setText(((Scenario) viewContext.get("Scenario")).getSource());
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