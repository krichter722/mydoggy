package org.noos.xing.mydoggy.plaf.ui.cmp;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RowPanel extends JPanel {
    protected int columns;

    public RowPanel(int columns, int colSize, int rowSize) {
        double[] layoutCols = new double[columns];
        for (int i = 0; i < columns; i++)
            layoutCols[i] = colSize;
        setLayout(new ExtendedTableLayout(new double[][]{layoutCols, new double[]{rowSize}}));
    }


    public void addEntry(int row, int column, Component component) {
        add(component, column + "," + row + ",r,c");
    }

    public void addColumnEntry(int column, Component component) {
        add(component, column + ",0,r,c");
    }

    public void addRowEntry(int row, Component component) {
        add(component, "0," + row + ",r,c");
    }
}