package org.noos.xing.mydoggy.plaf.common.context;

import org.noos.common.context.MutableContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultMutableContext implements MutableContext {
    protected Map map;

    public DefaultMutableContext() {
        this.map = new HashMap();
    }

    public DefaultMutableContext(Object... entries) {
        this();
        if (entries.length % 2 == 0) {
            for (int i = 0; i < entries.length; i += 2) {
                map.put(entries[i], entries[i + 1]);
            }
        } else
            throw new IllegalArgumentException("Invalid number of arguments. It must be even");

    }

    public void put(Object key, Object value) {
        map.put(key, value);
    }

    public Object remove(Object key) {
        return map.remove(key);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public <T> T get(Class<T> key) {
        return (T) map.get(key);
    }
}
