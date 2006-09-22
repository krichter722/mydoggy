package org.noos.xing.mydoggy;

import javax.swing.*;

/**
 * This interface is used to modify the behaviours of DOCKED type.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowType
 */
public interface DockedTypeDescriptor extends ToolWindowTypeDescriptor {

    void setPopupMenuEnabled(boolean enabled);

    boolean isPopupMenuEnabled();

    JMenu getUserDefinedMenu();


    void setDockLength(int length);

    int getDockLength();

}
