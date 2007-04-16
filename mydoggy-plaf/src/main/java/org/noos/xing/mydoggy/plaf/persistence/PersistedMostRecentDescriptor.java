package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.ToolWindowAnchor;

import java.util.Stack;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedMostRecentDescriptor {
    private Stack<ToolWindowAnchor> stack;

    public PersistedMostRecentDescriptor() {
        this.stack = new Stack<ToolWindowAnchor>();
    }

    public void push(ToolWindowAnchor anchor) {
        stack.push(anchor);
    }

    public Stack<ToolWindowAnchor> getStack() {
        return stack;
    }

    public void setStack(Stack<ToolWindowAnchor> stack) {
        this.stack = stack;
    }
}
