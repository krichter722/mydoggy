package org.noos.xing.mydoggy.tutorial;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleBarUI;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TutorialSet {
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
        customize();

        // Register a Tool.
        toolWindowManager.registerToolWindow("Debug",                      // Id
                                             "Debug Tool",                 // Title
                                             null,                         // Icon
                                             new JButton("Debug Tool"),    // Component
                                             ToolWindowAnchor.LEFT);       // Anchor

        // Add myDoggyToolWindowManager to the frame. MyDoggyToolWindowManager is an extension of a JPanel
        this.frame.getContentPane().add(myDoggyToolWindowManager, "1,1,");
    }

    protected void customize() {
        // Customize the toolwindow title bar
        // The releated UI is ToolWindowTitleBarUI

        // Change colors
        UIManager.put(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START, Color.RED);
        UIManager.put(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END,Color.RED.brighter().brighter());
        UIManager.put(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_ID_BACKGROUND_FLASHING_ON,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_ID_BACKGROUND_FLASHING_OFF,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_ID_BACKGROUND_ANIMATING,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_ID_BACKGROUND_ACTIVE,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_ID_BACKGROUND_INACTIVE,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED,Color.BLACK);
        UIManager.put(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED,Color.BLACK);


        // Modify the UI class
        UIManager.put("ToolWindowTitleBarUI", "org.noos.xing.mydoggy.tutorial.TutorialSet$CustomizedToolWindowTitleBarUI");
    }

    public static void main(String[] args) {
        TutorialSet test = new TutorialSet();
        try {
            test.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class CustomizedToolWindowTitleBarUI extends ToolWindowTitleBarUI {

        public static ComponentUI createUI(JComponent c) {
            return new CustomizedToolWindowTitleBarUI();
        }

        protected void updateToolWindowTitleBar(Graphics g, JComponent c, Color backgroundStart, Color backgroundEnd, Color idBackgroundColor, Color idColor) {
            Rectangle r = c.getBounds();
            r.x = r.y = 0;

            GraphicsUtil.fillRect(g, r,
                                  backgroundStart, backgroundEnd,
                                  null,
                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            if (descriptor.isIdVisibleOnTitleBar()) {
                int columWidth = getTitleWidth(g);
                if (columWidth != toolWindowTitleBarLayout.getColumn(0)) {
                    toolWindowTitleBarLayout.setColumn(0, columWidth);
                    SwingUtil.revalidate(toolWindowTitleBar);
                }

                String id = SwingUtil.getUserString(descriptor.getToolWindow().getRepresentativeAnchorButtonTitle());
                r.width = g.getFontMetrics().stringWidth(id) + 8;

                int halfHeigh = (r.height / 2);
                GraphicsUtil.fillRect(g, r,
                                      Color.WHITE,
                                      idBackgroundColor,
                                      new Polygon(new int[]{r.x, r.x + r.width - halfHeigh, r.x + r.width - halfHeigh, r.x},
                                                  new int[]{r.y, r.y, r.y + r.height, r.y + r.height},
                                                  4),
                                      GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

                Polygon polygon = new Polygon();
                polygon.addPoint(r.x + r.width - halfHeigh, r.y);
                polygon.addPoint(r.x + r.width - halfHeigh + 8, r.y + (r.height / 2));
                polygon.addPoint(r.x + r.width - halfHeigh, r.y + r.height);

                GraphicsUtil.fillRect(g, r,
                                      Color.WHITE,
                                      idBackgroundColor,
                                      polygon,
                                      GraphicsUtil.LEFT_TO_RIGHT_GRADIENT);

                g.setColor(idColor);
                g.drawString(id,
                             r.x + 2,
                             r.y + ((r.height - g.getFontMetrics().getHeight()) / 2) + g.getFontMetrics().getAscent());
            }

        }

    }
}