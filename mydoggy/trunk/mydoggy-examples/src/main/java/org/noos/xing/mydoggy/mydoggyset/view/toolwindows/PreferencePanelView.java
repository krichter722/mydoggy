package org.noos.xing.mydoggy.mydoggyset.view.toolwindows;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.tabs.ToolWindowTabsView;
import org.noos.xing.mydoggy.mydoggyset.view.toolwindows.types.TypeDescriptorsPreferenceView;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.action.ViewContextAction;
import org.noos.xing.yasaf.plaf.component.MatrixPanel;
import org.noos.xing.yasaf.plaf.view.ComponentView;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PreferencePanelView extends ComponentView {
    protected JPanel mainPanel;

    public PreferencePanelView(ViewContext viewContext) {
        super(viewContext);
    }

    protected Component initComponent() {
        mainPanel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {70, 1, -1}}));
        mainPanel.setBorder(new TitledBorder("Preferences"));
       
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("TypeDescriptors", new TypeDescriptorsPreferenceView(viewContext).getComponent());
        tabbedPane.add("ToolWindowTabs", new ToolWindowTabsView(viewContext).getComponent());

        mainPanel.add(new CommonPreferencePanelView(viewContext).getComponent(), "0,0,FULL,FULL");
        mainPanel.add(tabbedPane, "0,2,FULL,FULL");

        return mainPanel;
    }


    protected void initListeners() {
        viewContext.addViewContextChangeListener(ToolWindow.class, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                ToolWindow toolWindow = (ToolWindow) evt.getNewValue();

                if (toolWindow != null)
                    mainPanel.setBorder(new TitledBorder("Preference : " + toolWindow.getId()));
                else
                    mainPanel.setBorder(new TitledBorder("Preference : "));
            }
        });
    }

    public static class CommonPreferencePanelView extends ComponentView {
        enum Actions {
            SHOW_MESSAGE
        }

        protected JTextField messageField;

        public CommonPreferencePanelView(ViewContext viewContext) {
            super(viewContext);
        }

        protected Component initComponent() {
            MatrixPanel matrixPanel = new MatrixPanel(1, 1);

            JPanel showMessagePanel = new JPanel(new ExtendedTableLayout(new double[][]{{-1,1,70}, {-1}}));
            showMessagePanel.setBorder(new TitledBorder("Show Anchor Message"));
            showMessagePanel.add(messageField = new JTextField(), "0,0,FULL,FULL");
            showMessagePanel.add(new JButton(new ViewContextAction("Show", null, viewContext, Actions.SHOW_MESSAGE, ToolWindow.class)), "2,0,c,c");

            matrixPanel.addEntry(0,0, null, showMessagePanel);


            return matrixPanel;
        }


        protected void initListeners() {
            viewContext.addViewContextChangeListener(Actions.SHOW_MESSAGE, new ViewContextChangeListener() {
                public void contextChange(ViewContextChangeEvent evt) {
                    evt.getViewContext().get(ToolWindow.class).getRepresentativeAnchorDescriptor().showMessage(
                            null,
                            messageField.getText()
                    );
                }
            });
        }
    }

}
