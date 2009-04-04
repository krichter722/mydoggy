package org.noos.xing.mydoggy.scenarioset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.mydoggyset.action.ExitAction;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.persistence.PersistenceDelegateCallbackAdapter;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class LoadWorkspaceScenarioTest {

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
    }

    protected void start() {
        SwingUtil.centrePositionOnScreen(frame);
        frame.setVisible(true);
    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("ScenarioTestSet: LoadWorkspaceScenarioTest...");
        this.frame.setSize(640, 480);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a simple JMenuBar
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new LoadWorkspaceAction());
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(frame));


        menuBar.add(fileMenu);
        this.frame.setJMenuBar(menuBar);

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


    protected void initContentManager() {
        ContentManager contentManager = toolWindowManager.getContentManager();
        contentManager.setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());
    }

    public static void main(String[] args) {
        LoadWorkspaceScenarioTest test = new LoadWorkspaceScenarioTest();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected class LoadWorkspaceAction extends AbstractAction {

        public LoadWorkspaceAction() {
            super("Load Workspace");
        }

        public void actionPerformed(ActionEvent event) {
            File file = getFileSelection("load");
            if (file == null)
                return;
            try {
                FileInputStream fileInputStream = new FileInputStream(file);

                toolWindowManager.getPersistenceDelegate().merge(fileInputStream, PersistenceDelegate.MergePolicy.RESET,
                                                                 new PersistenceDelegateCallbackAdapter(){
                                                                     @Override
                                                                     public ToolWindow toolwindowNotFound(ToolWindowManager toolWindowManager, String toolWindowId, PersistenceNode node) {
                                                                         return toolWindowManager.registerToolWindow(toolWindowId, toolWindowId, null, new JButton(toolWindowId), ToolWindowAnchor.LEFT);
                                                                     }

                                                                     @Override
                                                                     public Content contentNotFound(ToolWindowManager toolWindowManager, String contentId, PersistenceNode node) {
                                                                         return toolWindowManager.getContentManager().addContent(contentId, contentId, null, new JButton(contentId));
                                                                     }
                                                                 });
                
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected File getFileSelection(String action) {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            if (fileChooser.showDialog(frame, action) == JFileChooser.CANCEL_OPTION)
                return null;
            return fileChooser.getSelectedFile();
        }

    }
}

