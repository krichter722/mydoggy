package org.noos.xing.mydoggy.examples.mydoggyset.action;

import org.noos.xing.mydoggy.examples.mydoggyset.MyDoggySet;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ChangeLookAndFeelAction extends AbstractAction {

    public static JMenuItem createLafMenuItem(MyDoggySet myDoggySet, JMenu menu, String label, String laf) {
        JMenuItem mi = menu.add(new JRadioButtonMenuItem(label));
        mi.setActionCommand(laf);
        mi.addActionListener(new ChangeLookAndFeelAction(myDoggySet));
        mi.setEnabled(isAvailableLookAndFeel(laf));
        return mi;
    }

    public static boolean isAvailableLookAndFeel(String laf) {
        try {
            Class lnfClass = Class.forName(laf);
            LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
            return newLAF.isSupportedLookAndFeel();
        } catch (Exception e) {
            return false;
        }
    }


    final MyDoggySet myDoggySet;

    protected ChangeLookAndFeelAction(MyDoggySet myDoggySet) {
        super("ChangeTheme");
        this.myDoggySet = myDoggySet;
    }

    public void actionPerformed(ActionEvent e) {
        myDoggySet.setLookAndFeel(e.getActionCommand());
    }
}
