package org.noos.xing.yasaf.plaf.bean;

import org.noos.xing.yasaf.bean.Invocation;
import org.noos.xing.yasaf.bean.Source;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultInvocation implements Invocation {
    protected Source target;
    protected Source args;

    public DefaultInvocation(Source target, Source args) {
        this.target = target;
        this.args = args;
    }

    public Object getTarget() {
        return target.getSource();
    }

    public Object[] getArgs() {
        return args.getSources();
    }
}
