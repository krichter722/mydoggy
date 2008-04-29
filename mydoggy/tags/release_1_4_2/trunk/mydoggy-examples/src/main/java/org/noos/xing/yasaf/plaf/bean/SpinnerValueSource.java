package org.noos.xing.yasaf.plaf.bean;

import org.noos.xing.yasaf.bean.Source;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SpinnerValueSource implements Source {
    private JSpinner spinner;

    public SpinnerValueSource(JSpinner spinner) {
        this.spinner = spinner;
    }

    public Object getSource() {
        return spinner.getValue();
    }

    public Object[] getSources() {
        return new Object[]{getSource()};
    }
}