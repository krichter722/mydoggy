package org.noos.xing.mydoggy.mydoggyset.view.interactive;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.itest.InteractiveTestRunner;
import org.noos.xing.mydoggy.itest.impl.SingleThreadInteractiveTestRunner;
import org.noos.xing.mydoggy.mydoggyset.view.interactive.tests.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.plaf.view.listener.ContextPutItemListener;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class TestChooserView extends ComponentView {

    public TestChooserView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        JPanel panel = new JPanel(new TableLayout(new double[][]{{-1}, {20,3,-1}}));
        panel.setBorder(new TitledBorder("Choose Interactive Test"));

        panel.add(new TestSelectorPanel(viewContext).getComponent(), "0,0,FULL,FULL");
        panel.add(new TestDescriptionPanel(viewContext).getComponent(), "0,2,FULL,FULL");

        return panel;
    }

    public class TestSelectorPanel extends ComponentView {
        protected JComboBox tests;

        public TestSelectorPanel(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JPanel panel = new JPanel(new TableLayout(new double[][]{{150,3,-1,3, 100},{-1}}));

            JFrame frame = viewContext.get(JFrame.class);
            ToolWindowManager toolWindowManager = viewContext.get(ToolWindowManager.class);

            try {
                tests = new JComboBox(new Object[]{
                        new InteractiveDragToTabTest(toolWindowManager, frame),
                        new InteractiveShowToolTest(frame, toolWindowManager),
                        new InteractiveRepresentativeDraggingTest(frame, toolWindowManager),
                        new InteractiveToolTypesTest(frame, toolWindowManager)
                });
            } catch (AWTException ignore) {
            }
            tests.addItemListener(new ContextPutItemListener(viewContext, InteractiveTest.class));

            JButton execute = new JButton("Execute");
            execute.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    InteractiveTest interactiveTest = viewContext.get(InteractiveTest.class);

                    InteractiveTestRunner runner = new SingleThreadInteractiveTestRunner();
                    runner.addInteractiveTest(interactiveTest);
                    runner.run();
                }
            });

            panel.add(new JLabel("Interactive Test : "), "0,0,r,FULL");
            panel.add(tests, "2,0,FULL,FULL");
            panel.add(execute , "4,0,FULL,FULL");

            return panel;
        }

        protected void onVisible() {
            tests.setSelectedItem(null);
            tests.setSelectedIndex(0);
        }
    }

    public class TestDescriptionPanel extends ComponentView {
        private JEditorPane editorPane;

        public TestDescriptionPanel(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            JPanel panel = new JPanel(new TableLayout(new double[][]{{-1},{-1}}));
            panel.setBorder(new LineBorder(Color.DARK_GRAY));

            this.editorPane = new JEditorPane();
            editorPane.setEditorKit(new HTMLEditorKit());
            editorPane.setEditable(false);

            panel.add(new JScrollPane(editorPane), "0,0,FULL,FULL");

            return panel;
        }

        protected void initListeners() {
            viewContext.addViewContextChangeListener(InteractiveTest.class, new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    InteractiveTest interactiveTest = (InteractiveTest) evt.getNewValue();
                    editorPane.setText(interactiveTest.getDescription());
                }
            });
        }
    }

}
