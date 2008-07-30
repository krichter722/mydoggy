package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.common.Question;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ParentOfQuestion implements Question<Component, Boolean> {
    protected Component parent;

    public ParentOfQuestion(Component parent) {
        this.parent = parent;
    }

    public Boolean getAnswer(Component param) {
        if (param == null)
            return false;

        Component cursor = param;
        while (cursor != null) {
            if (cursor == parent)
                return true;
            cursor = cursor.getParent();
        }
        return false;
    }

}
