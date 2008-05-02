package org.noos.xing.yasaf.plaf.bean;

import org.noos.xing.yasaf.bean.Source;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToFloatSource implements Source {
    protected Source source;

    public ToFloatSource(Source source) {
        this.source = source;
    }

    public Object getSource() {
        return Float.valueOf(source.getSource().toString());
    }

    public Object[] getSources() {
        Object[] sources = source.getSources();
        Float[] results = new Float[sources.length];
        for (int i = 0; i < sources.length; i++) {
            Object o = sources[i];
            results[i] = Float.valueOf(o.toString());
        }
        return results;
    }
}
