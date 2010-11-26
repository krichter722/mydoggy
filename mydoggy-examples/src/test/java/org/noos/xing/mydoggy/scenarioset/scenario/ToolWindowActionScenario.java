package org.noos.xing.mydoggy.scenarioset.scenario;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.scenario.AbstractScenario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowActionScenario extends AbstractScenario {

    protected JFrame frame;
    protected ToolWindowManager toolWindowManager;
    protected boolean setup = false;


    public String getName() {
        return getClass().getName();
    }

    public Window launch() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
        return frame;
    }

    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void setUp() {
        if (!setup) {
            initComponents();
            initToolWindowManager();

            setup = true;
        }
    }

    protected void start() {
        ToolWindow debugTool = toolWindowManager.getToolWindow("Debug");
        debugTool.setActive(true);

        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("TutorialSet...");
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
    }

    protected void initToolWindowManager() {
        // Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;

        // Register a Tool.
        ToolWindow toolWindow = toolWindowManager.registerToolWindow("Debug",                      // Id
                                                                     "Debug Tool",                 // Title
                                                                     null,                         // Icon
                                                                     new JButton("Debug Tool"),    // Component
                                                                     ToolWindowAnchor.LEFT);       // Anchor


        toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).addToolWindowAction(new CustomAction());

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }


    public class CustomAction extends ToolWindowAction {
        protected JMenuItem menuItem;

        public CustomAction() {
            super("Hello", UIManager.getIcon(MyDoggyKeySpace.TOOL_SCROLL_BAR_UP));
            setText("Helo");
//            setShowTextOnTitleBar(false);
        }

        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Hello World!!!");
        }

    }


}
