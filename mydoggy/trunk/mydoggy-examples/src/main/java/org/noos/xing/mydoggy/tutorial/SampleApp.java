package org.noos.xing.mydoggy.tutorial;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SampleApp {
    private JFrame frame;
    private ToolWindowManager toolWindowManager;

    protected void setUp() {
        initComponents();
        initToolWindowManager();
    }

    protected void start() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }

    protected void initComponents() {
        // This is need to a correct visualization of all JPopupMenu.
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        this.frame = new JFrame("Sample App...");
        this.frame.setSize(640, 480);
        this.frame.setLocation(100, 100);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // I love TableLayout. It's great.
        this.frame.getContentPane().setLayout(new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
    }

    protected void initToolWindowManager() {
        MyDoggyToolWindowManager myDoggyToolWindowManager = new MyDoggyToolWindowManager(frame);
        this.toolWindowManager = myDoggyToolWindowManager;

        toolWindowManager.registerToolWindow("Debug Tool",                           // Id
                                             "Debug Tool",                 // Title
                                             null,                          // Icon
                                             new JScrollPane(new JTable()),    // Component
                                             ToolWindowAnchor.LEFT);        // Anchor

        // Made all tools available
        for (ToolWindow window : toolWindowManager.getToolWindows())
            window.setAvailable(true);

        setupDebugTool();
        setupContentManager();

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }

    private void setupContentManager() {
        JButton b = new JButton(new AbstractAction("click") {

            public void actionPerformed(ActionEvent e) {
                toolWindowManager.getContentManager().addContent("Hello2", "Hello2", null, new JButton());
            }
        });

        toolWindowManager.getContentManager().addContent("Hello", "Hello", null, b);
    }

    protected void setupDebugTool() {
        ToolWindow debugTool = toolWindowManager.getToolWindow("Debug Tool");

        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.DOCKED);
        dockedTypeDescriptor.setDockLength(100);
        dockedTypeDescriptor.setPopupMenuEnabled(false);
        dockedTypeDescriptor.setToolWindowActionHandler(new ToolWindowActionHandler() {
            public void onHideButtonClick(ToolWindow toolWindow) {
                JOptionPane.showMessageDialog(frame, "Hiding...");
                toolWindow.setVisible(false);
            }
        });

        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.SLIDING);
        slidingTypeDescriptor.setEnabled(false);
        slidingTypeDescriptor.setTransparentMode(true);
        slidingTypeDescriptor.setTransparentRatio(0.8f);
        slidingTypeDescriptor.setTransparentDelay(0);

        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) debugTool.getTypeDescriptor(ToolWindowType.FLOATING);
        floatingTypeDescriptor.setEnabled(true);
        floatingTypeDescriptor.setLocation(150, 200);
        floatingTypeDescriptor.setSize(320, 200);
        floatingTypeDescriptor.setModal(false);
        floatingTypeDescriptor.setTransparentMode(true);
        floatingTypeDescriptor.setTransparentRatio(0.2f);
        floatingTypeDescriptor.setTransparentDelay(1000);
    }

    public static void main(String[] args) {
        SampleApp test = new SampleApp();
        try {
            test.setUp();
            test.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}