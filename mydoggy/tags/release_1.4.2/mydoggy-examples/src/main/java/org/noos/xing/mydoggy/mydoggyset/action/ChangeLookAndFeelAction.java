package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ChangeLookAndFeelAction extends AbstractAction {

    protected ViewContext viewContext;
    protected String laf;

    public ChangeLookAndFeelAction(ViewContext viewContext, String laf) {
        super("ChangeTheme");
        this.viewContext = viewContext;
        this.laf = laf;
    }

    public void actionPerformed(ActionEvent e) {
        viewContext.put(UIManager.class, laf);
    }
}
