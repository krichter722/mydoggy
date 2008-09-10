package org.noos.xing.mydoggy.mydoggyset;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FlashingTest extends JFrame {
    private ToolWindowManager doggy;

    private FlashingTest() {
        super("This is a test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        MyDoggyToolWindowManager doggy = new MyDoggyToolWindowManager();
        this.doggy = doggy;

        final ToolWindow win =
                doggy.registerToolWindow("Test", "This is a test", null,
                                         new JLabel("This is only a test"),
                                         ToolWindowAnchor.BOTTOM);
        win.setAvailable(true);

        JButton btn = new JButton("Test timed flashing");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                win.setFlashing(5000);
            }
        });

        JButton btn2 = new JButton("Test untimed flashing");
        btn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                win.setFlashing(!win.isFlashing());
            }
        });

        setContentPane(doggy);
        JPanel panel = new JPanel();
        panel.add(btn);
        panel.add(btn2);
        doggy.getContentManager().addContent("test", "Test content", null, panel);
    }

    public static void main(String[] args) {
        final FlashingTest test = new FlashingTest();
        test.setSize(640, 480);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                test.setVisible(true);
            }
        });
    }
}

 	  	 