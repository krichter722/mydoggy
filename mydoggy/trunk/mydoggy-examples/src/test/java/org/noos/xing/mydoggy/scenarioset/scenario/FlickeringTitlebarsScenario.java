package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * User: dkuffner
 * Date: Feb 3, 2009
 * Time: 2:39:23 PM
 */
public class FlickeringTitlebarsScenario extends JFrame {

    public FlickeringTitlebarsScenario() throws HeadlessException {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 500);

        MyDoggyToolWindowManager mgr = new MyDoggyToolWindowManager();
        getContentPane().add(mgr);


        //MyDoggyContentManager mgr2 = new MyDoggyContentManager(mgr);

        ToolWindow w1 = mgr.registerToolWindow("1", "1", null, new JLabel("1"), ToolWindowAnchor.BOTTOM);
        ToolWindow w2 = mgr.registerToolWindow("2", "2", null, new JLabel("2"), ToolWindowAnchor.TOP);
        ToolWindow w3 = mgr.registerToolWindow("3", "3", null, new JLabel("3"), ToolWindowAnchor.TOP);
        ToolWindow w4 = mgr.registerToolWindow("4", "4", null, new JLabel("4"), ToolWindowAnchor.LEFT);

        w1.setVisible(true);
        w2.setVisible(true);
        w3.setVisible(true);
        w3.aggregate(w4, AggregationPosition.TOP);
        w4.setVisible(true);
    }

    public static void main(String[] args) {
        new FlickeringTitlebarsScenario().setVisible(true);
    }
}
