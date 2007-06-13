package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.InteractiveAssertor;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SwingInteractiveAssertor implements InteractiveAssertor {
    private Container root;

    public SwingInteractiveAssertor(Container root) {
        this.root = root;
    }

    public void askForTrue(String message) {
        assertTrue(message, ask(message));
    }

    public void assertTrue(String s, boolean b) {
        if (!b)
            throw new IllegalStateException(s);
    }


    protected boolean ask(String message) {
        return JOptionPane.showConfirmDialog(root, message) == JOptionPane.OK_OPTION;
    }

}
