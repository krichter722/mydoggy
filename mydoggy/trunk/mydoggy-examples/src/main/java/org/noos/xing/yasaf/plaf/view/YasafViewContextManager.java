package org.noos.xing.yasaf.plaf.view;

import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextManager;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class YasafViewContextManager implements ViewContextManager {
    private static ViewContextManager instance = new YasafViewContextManager();

    public static ViewContextManager getInstance() {
        return instance;
    }

    private Map<Object, ViewContext> viewContextMap;

    public YasafViewContextManager() {
        viewContextMap = new Hashtable<Object, ViewContext>();
    }

    public void addViewContext(Object key, ViewContext viewContext) {
        viewContextMap.put(key, viewContext);
    }

    public ViewContext getViewContext(Object key) {
        return viewContextMap.get(key);
    }
}
