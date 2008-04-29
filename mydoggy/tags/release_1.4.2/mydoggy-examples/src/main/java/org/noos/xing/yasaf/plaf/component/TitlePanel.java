package org.noos.xing.yasaf.plaf.component;

import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TitlePanel extends JPanel {

    public TitlePanel(String title, Component component) {
        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        add(component, "0,0,FULL,FULL");
        setBorder(new TitledBorder(title));
    }
}
