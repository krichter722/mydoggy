package org.noos.xing.yasaf.plaf.component;

import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MatrixPanel extends JPanel {
    protected int columns;
    protected int rows;

    public MatrixPanel(int rows, int columns) {
        this(rows, columns, -1);
    }

    public MatrixPanel(int rows, int columns, int colValue) {
        this.columns = columns;
        this.rows = rows;

        double[] layoutCols = new double[1 + 4 * columns];
        double[] layoutRows = new double[2 + 2 * rows];

        layoutCols[0] = 3;
        for (int i = 0, index = 1; i < columns; i++) {
            layoutCols[index] = -2;
            layoutCols[index + 1] = 3;
            layoutCols[index + 2] = colValue;
            layoutCols[index + 3] = 3;
            index += 4;
        }

        layoutRows[0] = -1;
        layoutRows[layoutRows.length - 1] = -1;
        for (int i = 0, index = 1; i < rows; i++) {
            layoutRows[index] = -2;
            layoutRows[index + 1] = 3;
            index += 2;
        }
        setLayout(new ExtendedTableLayout(new double[][]{layoutCols, layoutRows}));
    }


    public void addEntry(int row, int column, String label, Component component) {
        column = 1 + column * 4;
        row = 1 + row * 2;

        if (label != null)
            add(new JLabel(label), column + "," + row + ",r,c");
        add(component, (column + 2) + "," + row + ",FULL,FULL");
    }

}
