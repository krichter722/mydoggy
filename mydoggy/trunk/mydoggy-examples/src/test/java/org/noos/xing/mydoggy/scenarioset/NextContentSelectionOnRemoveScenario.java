package org.noos.xing.mydoggy.scenarioset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.mydoggyset.action.ExitAction;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyTabbedContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.scenarioset.action.LoadWorkspaceAction;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class NextContentSelectionOnRemoveScenario {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;


    protected void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }

    protected void setUp() {
        initComponents();
        initToolWindowManager();
        initMenubar();
    }

    protected void start() {
        SwingUtil.centrePositionOnScreen(frame);
        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("ScenarioTestSet: NextContentSelectionOnRemoveScenario...");
        this.frame.setSize(640, 480);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set a layout manager. I love TableLayout. It's powerful.
        this.frame.getContentPane().setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
    }

    protected void initToolWindowManager() {
        // Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;

        initContentManager();

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }


    protected void initMenubar() {
        // Create a simple JMenuBar
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new LoadWorkspaceAction(frame, toolWindowManager));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(frame));

        menuBar.add(fileMenu);
        this.frame.setJMenuBar(menuBar);
    }

    protected void initContentManager() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        contentManager.setContentManagerUI(new MyDoggyTabbedContentManagerUI());
        contentManager.addContentManagerListener(new ContentManagerListener() {
            public void contentAdded(ContentManagerEvent event) {
                System.out.println("contentAdded = " + event);
            }

            public void contentRemoved(ContentManagerEvent event) {
                System.out.println("contentRemoved = " + event);
            }

            public void contentSelected(ContentManagerEvent event) {
                System.out.println("contentSelected = " + event);
            }
        });

        Content one = contentManager.addContent("1", "1", null, new JButton("1"), null, new MultiSplitConstraint(AggregationPosition.TOP));
        Content two = contentManager.addContent("2", "2", null, new JButton("2"), null, new MultiSplitConstraint(one));


        Content three = contentManager.addContent("3", "3", null, new JButton("3"), null, new MultiSplitConstraint(AggregationPosition.BOTTOM));
        Content four = contentManager.addContent("4", "4", null, new JButton("4"), null, new MultiSplitConstraint(three));

    }

    public static void main(String[] args) {
        NextContentSelectionOnRemoveScenario test = new NextContentSelectionOnRemoveScenario();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}