package org.noos.xing.mydoggy.plaf.ui.util;

import org.jdesktop.swingx.JXMonthView;
import org.noos.common.Question;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ParentOfQuestion implements Question {
    protected Component parent;

    public ParentOfQuestion(Component parent) {
        this.parent = parent;
    }

    public boolean is(Object... params) {
        if (params.length == 0)
            return false;

        Component component = (Component) params[0];
        if (component == null)
            return false;

//        System.out.println("--------------------------------------------------------");
        Component traverser = component;
        while (traverser.getParent() != null) {
//            System.out.println("traverser : " + traverser);
            if (traverser instanceof JXMonthView) {
                System.out.println("OK");
                return true;
            }
            if (traverser.getParent() == parent) {
//                System.out.println("--------------------------------------------------------");
                return true;
            }
            traverser = traverser.getParent();
        }
//        System.out.println("--------------------------------------------------------");
        return false;
    }
}
