package org.noos.xing.yasaf.plaf.bean;

import org.noos.xing.yasaf.bean.Source;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AndSource implements Source {
    protected Source one, two;

    public AndSource(Source one, Source two) {
        this.one = one;
        this.two = two;
    }

    public Object getSource() {
        return one.getSource();
    }

    public Object[] getSources() {
        return new Object[]{one.getSource(), two.getSource()};
    }
}
