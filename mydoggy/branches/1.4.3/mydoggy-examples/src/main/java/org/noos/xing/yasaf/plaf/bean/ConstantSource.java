package org.noos.xing.yasaf.plaf.bean;

import org.noos.xing.yasaf.bean.Source;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ConstantSource implements Source {
    private Object o;

    public ConstantSource(Object o) {
        this.o = o;
    }

    public Object getSource() {
        return o;
    }

    public Object[] getSources() {
        return new Object[]{o};
    }
}
