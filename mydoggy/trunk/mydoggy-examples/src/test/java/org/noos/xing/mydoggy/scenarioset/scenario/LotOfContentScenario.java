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
public class LotOfContentScenario extends AbstractScenario {

    double p = TableLayout.PREFERRED;
    double f = TableLayout.FILL;

    MyDoggyToolWindowManager toolW;
    JFrame frame = new JFrame();

    public String getName() {
        return LotOfContentScenario.class.getName();
    }

    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Window launch() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                double size[][] = {{f}, {p, 2, f}};

                JPanel content = new JPanel(new TableLayout(size));
                content.add(buildToolBar(), "0,0");
                content.add(buildToolWindow(), "0,2");

                frame.getContentPane().add(content);
                frame.setSize(800, 600);

                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//                frame.pack();
                frame.setVisible(true);
            }
        });
        return frame;
    }


    public JPanel buildToolBar() {
        double[][] size = {{p, 2, p, 2, p}, {p}};
        JPanel ret = new JPanel(new TableLayout(size));

        JButton save = new JButton("save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                File file = getFileSelection("save");
                if (file == null)
                    return;
                try {
                    toolW.getPersistenceDelegate().save(new FileOutputStream(file));
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
                    toolW.getPersistenceDelegate().apply(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });
        ret.add(load, "2,0");

        return ret;
    }

    public MyDoggyToolWindowManager buildToolWindow() {
        this.toolW = new MyDoggyToolWindowManager();
        ContentManager contentManager = toolW.getContentManager();

        MyDoggyMultiSplitContentManagerUI contentUI = new MyDoggyMultiSplitContentManagerUI();
        this.toolW.getContentManager().setContentManagerUI(contentUI);


        for (int i = 0; i < 18; i++) {
            JPanel pane = new JPanel();
            //	pane.setPreferredSize(new Dimension(400,400));
            Content cont = contentManager.addContent("content" + i, "content" + i,
                                                     null, pane);
        }

        return this.toolW;
    }


    private File getFileSelection(String action) {
        JFileChooser fileChooser = new JFileChooser();

        int returnVal = fileChooser.showDialog(frame, action);
        if (returnVal == JFileChooser.CANCEL_OPTION)
            return null;

        File selected = fileChooser.getSelectedFile();
        return selected;
    }

}
