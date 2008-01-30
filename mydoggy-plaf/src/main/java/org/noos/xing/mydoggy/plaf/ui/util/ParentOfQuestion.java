package org.noos.xing.mydoggy.plaf.ui.util;

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

        Component cursor = component;
        while (cursor != null) {
            if (cursor == parent)
                return true;
            cursor = cursor.getParent();
        }
        return false;
    }
}
