package org.noos.xing.yasaf.plaf.bean;

import org.noos.xing.yasaf.bean.Source;

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

    public Object[] getSources() {
        return new Object[]{getSource()};
    }
}
