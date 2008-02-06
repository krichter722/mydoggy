package org.noos.xing.mydoggy.plaf;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Angelo De Caro  (angelo.decaro@gmail.com)
 */
public class TestJTabbedContentPane extends TestCase {

    public void testOne() {
        JTabbedContentPane tabbedContentPane = new JTabbedContentPane();
        tabbedContentPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("e = " + e);
            }
        });
        tabbedContentPane.addTab("Title 1", new JButton(""));
        tabbedContentPane.addTab("Title 2", new JButton(""));
        tabbedContentPane.setSelectedIndex(0);
        tabbedContentPane.removeTabAt(0);
        System.out.printf("OK");
    }
}
