package org.noos.xing.mydoggy.examples.mydoggyset.action;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ChecBoxSelectionSource implements Source {
    private JCheckBox checkBox;

    public ChecBoxSelectionSource(JCheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Object getSource() {
        return checkBox.isSelected();
    }
}
