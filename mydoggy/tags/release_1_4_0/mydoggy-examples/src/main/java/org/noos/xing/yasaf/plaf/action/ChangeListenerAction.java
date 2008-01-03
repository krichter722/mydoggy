package org.noos.xing.yasaf.plaf.action;

import org.noos.xing.yasaf.plaf.action.DynamicAction;
import org.noos.xing.yasaf.bean.Source;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ChangeListenerAction extends DynamicAction implements ChangeListener {

    public ChangeListenerAction(Class targetClass, String property, Source target, Source args) {
        super(targetClass, property, target, args);
    }

    public void stateChanged(ChangeEvent e) {
        actionPerformed(new ActionEvent(this, 0, null));
    }
    
}
