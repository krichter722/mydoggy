package org.noos.xing.mydoggy.scenarioset.scenario;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyMultiSplitContentManagerUI;
import org.noos.xing.mydoggy.scenario.AbstractScenario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SaveRestoreContentsScenario extends AbstractScenario {

    double p = TableLayout.PREFERRED;
    double f = TableLayout.FILL;

    protected JFrame frame = new JFrame();
    protected MyDoggyToolWindowManager toolWindowManager;
    protected boolean setup = false;


    public String getName() {
        return this.getClass().getName();
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

    public static void main(String[] args) {
    }

    protected void setUp() {
        if (!setup) {
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            double size[][] = {{f}, {p, 2, f}};

            JPanel content = new JPanel(new TableLayout(size));
            content.add(this.buildToolBar(), "0,0");
            content.add(this.buildToolWindow(), "0,2");

            frame.getContentPane().add(content);
            frame.pack();

            setup = true;
        }
    }

    protected void start() {
        frame.setVisible(true);
    }


    protected JPanel buildToolBar() {
        double[][] size = {{p, 2, p, 2, p}, {p}};
        JPanel ret = new JPanel(new TableLayout(size));

        JButton save = new JButton("save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                File file = getFileSelection("save");
                if (file == null)
                    return;
                try {
                    toolWindowManager.getPersistenceDelegate().save(new FileOutputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });
        ret.add(save, "0,0");


        JButton load = new JButton("load");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                File file = getFileSelection("load");
                if (file == null)
                    return;
                try {
                    toolWindowManager.getPersistenceDelegate().apply(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });
        ret.add(load, "2,0");

        return ret;
    }

    protected MyDoggyToolWindowManager buildToolWindow() {
        this.toolWindowManager = new MyDoggyToolWindowManager();
        ContentManager contentManager = toolWindowManager.getContentManager();

        for (int i = 0; i < 8; i++) {
            JPanel pane = new JPanel();
            //	pane.setPreferredSize(new Dimension(400,400));
            Content cont = contentManager.addContent("content" + i, "content" + i, null, pane);
        }


        MyDoggyMultiSplitContentManagerUI contentUI = new MyDoggyMultiSplitContentManagerUI();
        this.toolWindowManager.getContentManager().setContentManagerUI(contentUI);

        return this.toolWindowManager;
    }

    protected File getFileSelection(String action) {
        JFileChooser fileChooser = new JFileChooser();

        int returnVal = fileChooser.showDialog(frame, action);
        if (returnVal == JFileChooser.CANCEL_OPTION)
            return null;

        File selected = fileChooser.getSelectedFile();
        return selected;
    }

}
