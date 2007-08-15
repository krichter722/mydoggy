package org.noos.xing.mydoggy.examples.mydoggyset.content;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.examples.mydoggyset.content.interactive.InteractiveDragTest;
import org.noos.xing.mydoggy.examples.mydoggyset.content.interactive.InteractiveSimpleTest;
import org.noos.xing.mydoggy.examples.mydoggyset.content.interactive.InteractiveToolVisisbleTest;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.itest.InteractiveTestRunner;
import org.noos.xing.mydoggy.itest.impl.SingleThreadInteractiveTestRunner;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveTestContentComponent extends JPanel {
    private ToolWindowManager toolWindowManager;
    private JTextComponent testDescription;
    private JFrame frame;

    public InteractiveTestContentComponent(JFrame frame, ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        this.frame = frame;
        initComponents();
    }

    protected void initComponents() {
        // Preference Panel
        JPanel prefPanel = new JPanel(new TableLayout(new double[][]{{-1},{20,3,-1}}));
        prefPanel.setBorder(new TitledBorder("Interactive Tests"));

        prefPanel.add(initContentUIManagerPrefPanel(), "0,2,FULL,FULL");
        prefPanel.add(initTestsSelectorPanel(), "0,0,FULL,FULL");


        // Setup main panel
        setLayout(new TableLayout(new double[][]{{-1},{-1}}));
        add(prefPanel, "0,0,FULL,FULL");
    }

    protected JPanel initContentUIManagerPrefPanel() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{-1},{-1}}));
        panel.setBorder(new LineBorder(Color.DARK_GRAY));

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        this.testDescription = editorPane;

        panel.add(new JScrollPane(testDescription), "0,0,FULL,FULL");

        return panel;
    }

    protected JPanel initTestsSelectorPanel() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{150,3,-1,3, 100},{-1}}));

        final JComboBox tests = new JComboBox(new Object[]{
                new InteractiveToolVisisbleTest(frame),
                new InteractiveDragTest(frame),
                new InteractiveSimpleTest(frame)
        });
        tests.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                InteractiveTest interactiveTest = (InteractiveTest) e.getItem();
                testDescription.setText(interactiveTest.getDescription());
            }
        });
        tests.setSelectedIndex(0);

        JButton execute = new JButton("Execute");
        execute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                InteractiveTest interactiveTest = (InteractiveTest) tests.getSelectedItem();

                InteractiveTestRunner runner = new SingleThreadInteractiveTestRunner();
                runner.addInteractiveTest(interactiveTest);
                runner.run();
            }
        });

        panel.add(new JLabel("ContentManagerUI : "), "0,0,r,FULL");
        panel.add(tests, "2,0,FULL,FULL");
        panel.add(execute , "4,0,FULL,FULL");                

        return panel;
    }

}