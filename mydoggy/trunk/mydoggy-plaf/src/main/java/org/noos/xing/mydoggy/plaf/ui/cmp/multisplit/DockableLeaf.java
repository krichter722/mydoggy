package org.noos.xing.mydoggy.plaf.ui.cmp.multisplit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DockableLeaf extends MultiSplitLayout.Leaf {
    private List<String> dockables;


    public DockableLeaf() {
    }

    public DockableLeaf(String name) {
        super(name);
        this.dockables = new ArrayList<String>();
    }

    public DockableLeaf(String name, String dockableId) {
        super(name);
        this.dockables = new ArrayList<String>();
        this.dockables.add(dockableId);
    }


    public String getDockable() {
        return dockables.get(0);
    }

    public List<String> getDockables() {
        return dockables;
    }

    public void setDockables(List<String> dockables) {
        this.dockables = dockables;
    }

    public void addDockable(String dockableId) {
        dockables.add(dockableId);
    }

    public int getNameValue() {
        return Integer.parseInt(getName());
    }
}
