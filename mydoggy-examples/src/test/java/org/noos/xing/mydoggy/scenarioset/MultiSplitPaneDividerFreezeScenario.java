package org.noos.xing.mydoggy.scenarioset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.mydoggyset.action.ExitAction;
import org.noos.xing.mydoggy.mydoggyset.action.StoreWorkspaceAction;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.scenarioset.action.LoadWorkspaceAction;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultiSplitPaneDividerFreezeScenario {

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
        this.frame = new JFrame("ScenarioTestSet: LoadWorkspaceScenario...");
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
        fileMenu.add(new StoreWorkspaceAction(frame, toolWindowManager));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(frame));

        menuBar.add(fileMenu);
        this.frame.setJMenuBar(menuBar);
    }


    protected void initContentManager() {
        final ContentManager contentManager = toolWindowManager.getContentManager();
        TabbedContentManagerUI contentManagerUI = new MyDoggyMultiSplitContentManagerUI();
        contentManager.setContentManagerUI(contentManagerUI);
        contentManagerUI.setPopupMenuEnabled(false);
        contentManagerUI.setMinimizable(false);
        contentManagerUI.setMaximizable(false);
        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.TOP);
        contentManagerUI.setShowAlwaysTab(true);
        contentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);

        for (int index = 0; index < 4; index++) {
            String contentId = "Content_" + index;
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(100, 100));
            contentManager.addContent(contentId, contentId, null, panel);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                /*
                    1. DnD Content_0 to the right
                    2. DnD Content_1 to the bottom
                    3. DnD Content_0 to the right
                */
                contentManager.getContent("Content_0").getContentUI().setConstraints(new MultiSplitConstraint(AggregationPosition.RIGHT));
                contentManager.getContent("Content_1").getContentUI().setConstraints(new MultiSplitConstraint(AggregationPosition.BOTTOM));
                contentManager.getContent("Content_0").getContentUI().setConstraints(new MultiSplitConstraint(AggregationPosition.RIGHT));
            }
        });
    }

    public static void main(String[] args) {
        MultiSplitPaneDividerFreezeScenario test = new MultiSplitPaneDividerFreezeScenario();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}