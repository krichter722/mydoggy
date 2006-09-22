package org.noos.xing.mydoggy.plaf.ui.layout;

import info.clearthought.layout.TableLayout;

import java.awt.*;
import java.util.ListIterator;

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

    public ExtendedTableLayout(boolean calcMinimumSize) {
        this.calcMinimumSize = calcMinimumSize;
    }

    public Dimension minimumLayoutSize(Container container) {
        return (calcMinimumSize) ? super.minimumLayoutSize(container) : empty;
    }

    public void removeLayoutComponent(Component component) {
        ListIterator iterator = list.listIterator(0);
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            if (entry.equals(component)) {
                iterator.remove();
                break;
            }
        }
    }

    public void drawGrid(Container container, Graphics g) {
        int counter; // Counting variable;

        // Calculate the sizes of the rows and columns
        Dimension d = container.getSize();

        if (dirty || (d.width != oldWidth) || (d.height != oldHeight))
            calculateSize(container);

        // Initialize y
        int y = 0;

        for (int row = 0; row < crSize[R].length; row++) {
            // Initialize x
            int x = 0;

            for (int column = 0; column < crSize[C].length; column++) {
                // Use a random color to make things easy to see
                Color color = new Color((int) (Math.random() * 0xFFFFFFL));
                g.setColor(color);

                // Draw the cell as a solid rectangle
                g.fillRect(x, y, crSize[C][column], crSize[R][row]);

                // Increment x
                x += crSize[C][column];
            }

            // Increment y
            y += crSize[R][row];
        }
    }

    public int[] getRowsInPixel() {
        return crSize[R];
    }

    public int[] getColsInPixel() {
        return crSize[C];
    }

}
