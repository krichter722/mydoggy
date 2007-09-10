package org.noos.xing.yasaf.plaf.action;

import org.noos.xing.yasaf.bean.Source;
import org.noos.xing.yasaf.view.ViewContext;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ViewContextSource implements Source {

    protected ViewContext viewContext;
    protected Object key;

    public ViewContextSource(ViewContext viewContext, Object key) {
        this.viewContext = viewContext;
        this.key = key;
    }

    public Object getSource() {
        return viewContext.get(key);
    }

    public Object[] getSources() {
        return new Object[]{getSource()};
    }

}
