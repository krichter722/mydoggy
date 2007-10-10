package org.noos.xing.yasaf.plaf.component;

import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TitlePanel extends JPanel {

    public TitlePanel(String title, Component component) {
        setLayout(new TableLayout(new double[][]{{-1}, {-1}}));
        add(component, "0,0,FULL,FULL");
        setBorder(new TitledBorder(title));
    }
}
