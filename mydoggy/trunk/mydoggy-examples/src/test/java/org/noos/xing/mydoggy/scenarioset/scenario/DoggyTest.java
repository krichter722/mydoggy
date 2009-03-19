package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

public class DoggyTest extends JFrame {

    JFrame getFrame() {
        return this;
    }

    public DoggyTest() {
        super("Doggy Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        creatMenubar();
        add(new DoggyPanel(), BorderLayout.CENTER);
        setSize(400, 400);

    }

    public void creatMenubar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu test = new JMenu("test");
        JMenuItem save = new JMenuItem("Save");

        AbstractAction a = new AbstractAction("Save") {

            public void actionPerformed(ActionEvent e) {
                System.out.println("Save should always do");
                JOptionPane.showMessageDialog(DoggyTest.this, "It works");

            }

        };
        final String actionMapKey = "triggerActionListener";
        save.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), actionMapKey);
        save.getActionMap().put(actionMapKey, a);
        save.setAction(a);
        test.add(save);
        menuBar.add(test);
        setJMenuBar(menuBar);
    }

    class DoggyPanel extends JPanel {

        private MyDoggyToolWindowManager toolWindowManager;

        public DoggyPanel() {
            setLayout(new BorderLayout());
            JPanel panel = new JPanel();
            panel.setLayout(new ExtendedTableLayout(new double[][] { { 0, -1, 0 }, { 0, -1, 0 } }));

            toolWindowManager = new MyDoggyToolWindowManager(Locale.getDefault(), this.getClass().getClassLoader());
            ContentManagerUI defaultManagerUI = toolWindowManager.getContentManager().getContentManagerUI();
            TabbedContentManagerUI tabbedContentManagerUI = (TabbedContentManagerUI) defaultManagerUI;
            tabbedContentManagerUI.setShowAlwaysTab(false);
            tabbedContentManagerUI.setDetachable(false);
            tabbedContentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.TOP);
            tabbedContentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);
            intitPanels();

            // panel.add(new JButton(new AbstractAction("Hello") {
            //
            //
            // public void actionPerformed(ActionEvent e) {
            // JDialog dialog = new JDialog(DoggyTest.this);
            // dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            // dialog.setSize(320,200);
            // dialog.setVisible(true);
            // }
            // }), "1,1,FULL,FULL");

            panel.add(toolWindowManager, "1,1,");

            add(panel, BorderLayout.CENTER);
        }

        private void intitPanels() {

            for (int i = 0; i < 2; i++) {
                ToolWindow toolWindow = toolWindowManager.registerToolWindow("Tool " + String.valueOf(i), "Tool", null, createPanel(), ToolWindowAnchor.LEFT);
                toolWindow.setAvailable(true);
                toolWindow.setActive(false);

                // toolWindow.getRepresentativeAnchorDescriptor().setPreviewEnabled(false);
            }

        }

        private JPanel createPanel() {
            JPanel panel = new JPanel();
            for (int i = 0; i < 5; i++) {
                panel.add(new JButton(new AbstractAction("Hello") {

                    public void actionPerformed(ActionEvent e) {
                        JDialog dialog = new JDialog(DoggyTest.this);
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setSize(320, 200);
                        dialog.setVisible(true);
                    }
                }));
            }

            return panel;
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new DoggyTest().setVisible(true);

            }

        });

    }

}