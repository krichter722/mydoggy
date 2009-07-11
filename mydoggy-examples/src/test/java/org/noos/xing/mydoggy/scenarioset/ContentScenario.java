package org.noos.xing.mydoggy.scenarioset;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.action.ExitAction;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.scenarioset.action.LoadWorkspaceAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentScenario {

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
        this.frame = new JFrame("ScenarioTestSet: ContentScenario...");
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


        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(myDoggyToolWindowManager, BorderLayout.CENTER);

        JButton b = new JButton("update UI");
        b.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SwingUtilities.updateComponentTreeUI((Component) toolWindowManager);

            }
        });
        contentPane.add(b, BorderLayout.SOUTH);
        JButton a = new JButton("Add content");
        a.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String generatedTitle =
                        String.valueOf(System.identityHashCode(new Object()));
                JLabel component = new JLabel(generatedTitle);
                component.setSize(200, 200);
                component.setPreferredSize(new Dimension(200, 200));
                Content addContent = toolWindowManager.getContentManager().addContent(generatedTitle, "Title : " + generatedTitle, null, component);
                addContent.setSelected(true);

            }
        });
        contentPane.add(a, BorderLayout.NORTH);

        this.frame.setContentPane(contentPane);
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
// contentManager.setContentManagerUI(new MyDoggyDesktopContentManagerUI());
// contentManager.setContentManagerUI(new MyDoggyMultiSplitContentManagerUI());
    }

    public static void main(String[] args) {
        ContentScenario test = new ContentScenario();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}