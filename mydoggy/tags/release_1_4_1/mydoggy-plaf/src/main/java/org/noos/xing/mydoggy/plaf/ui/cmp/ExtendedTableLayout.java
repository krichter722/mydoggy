package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;

import java.awt.*;

/**
 * @author Angelo De Caro
 */
public class ExtendedTableLayout extends TableLayout {
    private static final Dimension empty = new Dimension();
    private boolean calcMinimumSize = false;

    public ExtendedTableLayout() {
    }

    public ExtendedTableLayout(double size[][]) {
        super(size);
    }

    public ExtendedTableLayout(double size[][], boolean calcMinimumSize) {
        super(size);
        this.calcMinimumSize = calcMinimumSize;
    }

    public ExtendedTableLayout(boolean calcMinimumSize) {
        this.calcMinimumSize = calcMinimumSize;
    }

    public Dimension minimumLayoutSize(Container container) {
        return (calcMinimumSize) ? super.minimumLayoutSize(container) : empty;
    }

    public int[] getRowsInPixel() {
        return crSize[R];
    }

    public int[] getColsInPixel() {
        return crSize[C];
    }

}
