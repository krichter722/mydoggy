package org.noos.xing.mydoggy.mydoggyset.ui;

import org.noos.xing.mydoggy.mydoggyset.action.ChangeLookAndFeelAction;
import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class LookAndFeelMenuItem extends JRadioButtonMenuItem {

    public static boolean isAvailableLookAndFeel(String laf) {
        try {
            Class lnfClass = Class.forName(laf);
            LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
            return newLAF.isSupportedLookAndFeel();
        } catch (Exception e) {
            return false;
        }
    }


    public LookAndFeelMenuItem(ViewContext viewContext, String label, final String lafClass) {
        super(label);
        
        setActionCommand(lafClass);
        addActionListener(new ChangeLookAndFeelAction(viewContext, lafClass));
        setEnabled(isAvailableLookAndFeel(lafClass));

        viewContext.addViewContextChangeListener(UIManager.class, new ViewContextChangeListener() {
            public void contextChange(ViewContextChangeEvent evt) {
                String laf = (String) evt.getNewValue();
                setSelected((laf.equals(lafClass)));
            }
        });
    }

}
