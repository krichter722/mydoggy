package org.noos.xing.mydoggy.mydoggyset.multisplit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JustForTest {

    public static void main(String[] args) {

        final JFrame root = new JFrame("Root");
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JButton button = new JButton("Hello");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(root, false);
                dialog.setLocation(100,100);
                dialog.setSize(100,100);
                dialog.setVisible(true);

                button.setFont(button.getFont().deriveFont(35));
            }
        });

        root.add(new StyleOptionsPanel());
        root.add(button);
//        root.setUndecorated(true);
//        root.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        root.pack();
        root.setLocationByPlatform(true);
        root.setVisible(true);

//        JWindow window = new JWindow();
    }


    public static class StyleOptionsPanel extends JPanel {
        private JLabel saying;
        private JCheckBox bold, italic;


        public StyleOptionsPanel() {
            saying = new JLabel("Say it with style!");
            saying.setFont(new Font("Helvetica", Font.PLAIN, 36));

            bold = new JCheckBox("Bold");
            bold.setBackground(Color.cyan);
            italic = new JCheckBox("Italic");
            italic.setBackground(Color.cyan);


            StyleListener listener = new StyleListener();
            bold.addItemListener(listener);
            italic.addItemListener(listener);

            add(saying);
            add(bold);
            add(italic);

            setBackground(Color.cyan);
            setPreferredSize(new Dimension(300, 100));
        }

        private class StyleListener implements ItemListener {
//--------------------------------------------------------------
// Updates the style of the label font style.

            //--------------------------------------------------------------
            public void itemStateChanged(ItemEvent event) {
                int style = Font.PLAIN;

                if (bold.isSelected())
                    style = Font.BOLD;

                if (italic.isSelected())
                    style += Font.ITALIC;

                saying.setFont(
                        saying.getFont().deriveFont(13f)
                        /*new Font("Helvetica", style, 36)*/);
            }
        }
    }
}

