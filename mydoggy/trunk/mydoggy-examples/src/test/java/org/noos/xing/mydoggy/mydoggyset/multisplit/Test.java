package org.noos.xing.mydoggy.mydoggyset.multisplit;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.action.ExitAction;
import org.noos.xing.mydoggy.mydoggyset.action.LoadWorkspaceAction;
import org.noos.xing.mydoggy.mydoggyset.action.StoreWorkspaceAction;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Test used to reproduce an infite-loop bug where two ToolWindows keep getting the focus alternatively.
 * <p/>
 * To produce the bug, just drag and drop the Tool_2 ToolWindow to the bottom of the Tool_1.
 * The Tool_2 must not have the focus when you start draging.
 *
 * @author Jean Morissette (jean.morissette@gmail.com)
 */
public class Test {

    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        frame.setVisible(true);

    }

    protected void initComponents() {
        // Init the frame
        this.frame = new JFrame("Sample App...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.frame.getContentPane().setLayout(new BorderLayout());

    }

    protected void initToolWindowManager() {
// Create a new instance of MyDoggyToolWindowManager passing the frame.
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager();
        this.toolWindowManager = myDoggyToolWindowManager;

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, BorderLayout.CENTER);

        /* errors */
        JButton view = new JButton("H");

        ToolWindow toolWindow =
                toolWindowManager.registerToolWindow("ErrorsView.title", "Title 1",
                                                     view.getIcon(), (Component) view, ToolWindowAnchor.BOTTOM);
        toolWindow.setAvailable(true);
//        toolWindow.setAutoHide(true);
//        toolWindow.setType(ToolWindowType.SLIDING);

        DockedTypeDescriptor dockedTypeDescriptor =
                toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
//        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);
        dockedTypeDescriptor.setDockLength(400);
//        toolWindow.getTypeDescriptor(SlidingTypeDescriptor.class).setIdVisibleOnTitleBar(false);

        /* report */
        view = new JButton("H");

        toolWindow = toolWindowManager.registerToolWindow("ReportView.title",
                                                          "title 2", view.getIcon(), (Component) view, ToolWindowAnchor.RIGHT);
        toolWindow.setAvailable(true);
//        toolWindow.setAutoHide(true);
//        toolWindow.setType(ToolWindowType.SLIDING);

        dockedTypeDescriptor =
                toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
//        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);
        dockedTypeDescriptor.setDockLength(400);
//        toolWindow.getTypeDescriptor(SlidingTypeDescriptor.class).setIdVisibleOnTitleBar(false);

        // Made all tools available
        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        toolWindowManager.getContentManager().addContent("id", "tit", null, new JButton("H"));


        initMenuBar();
    }

    protected void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new LoadWorkspaceAction(null, frame, toolWindowManager));
        fileMenu.add(new StoreWorkspaceAction(frame, toolWindowManager));
        fileMenu.addSeparator();
        fileMenu.add(new ExitAction(frame));

        menuBar.add(fileMenu);

        this.frame.setJMenuBar(menuBar);
    }

    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }

    public static void main(String[] args) throws IOException {
        Test test = new Test();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}