package org.noos.xing.mydoggy.mydoggyset.ui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import org.jdesktop.swingx.JXTitledPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class NavigationPanel extends JPanel {

    protected JXTitledPanel navigatorPanel = null;
    protected JXTitledPanel infoPanel = null;


    public NavigationPanel() {
        super();
        init();
    }

    /**
     * Initialize the main panel
     */
    protected void init() {
        navigatorPanel = new JXTitledPanel("Navigation");


        JPanel jPanel = new JPanel();
        double size[][] = {{TableLayoutConstants.FILL}, // Columns
                {25, 25, 25, 25, TableLayoutConstants.FILL}}; // Rows
        jPanel.setLayout(new TableLayout(size));
        jPanel.add(new JTextField("textfield1"), "0,0");
        jPanel.add(new JTextField("textfield2"), "0,1");
        jPanel.add(new JTextField("textfield3"), "0,2");
        jPanel.add(new JTextField("textfield4"), "0,3");
        infoPanel = new JXTitledPanel("Information", jPanel);


        JSplitPane mainSP = createSplitPane(200, JSplitPane.HORIZONTAL_SPLIT);
        mainSP.setLeftComponent(navigatorPanel);
        mainSP.setRightComponent(infoPanel);

        this.setLayout(new BorderLayout());
        this.add(infoPanel);
    }

    private JSplitPane createSplitPane(int dividerLocation, int orientation) {
        JSplitPane splitPane = new JSplitPane(orientation);
        splitPane.setDividerLocation(dividerLocation);
        splitPane.setBorder(null);
        ((BasicSplitPaneUI) splitPane.getUI()).getDivider().setBorder(BorderFactory.createEmptyBorder());
        return splitPane;
    }


}
