package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultDockedTypeDescriptor extends DefaultToolWindowTypeDescriptor implements DockedTypeDescriptor,
                                                                                            InternalTypeDescriptor {
    protected ToolWindowActionHandler toolWindowActionHandler;

    protected boolean popupMenuEnabled;
    protected JMenu toolsMenu;

    protected int dockLength;
    protected int minimumDockLength;

    protected boolean hideRepresentativeButtonOnVisible;


    public DefaultDockedTypeDescriptor() {
        this.toolsMenu = new JMenu(SwingUtil.getString("@@tool.toolsMenu"));
        this.popupMenuEnabled = true;
        this.dockLength = 200;
        this.toolWindowActionHandler = null;
        this.animating = true;
        this.autoHide = false;
        this.hideRepresentativeButtonOnVisible = false;
        this.idVisibleOnTitleBar = true;
        this.minimumDockLength = 100;
        this.toolWindowActionMap = new HashMap<String, ToolWindowAction>();
    }

    public DefaultDockedTypeDescriptor(ToolWindowDescriptor toolWindowDescriptor,
                                       DefaultDockedTypeDescriptor parent,
                                       int dockLength, boolean popupMenuEnabled,
                                       ToolWindowActionHandler toolWindowActionHandler, boolean animating,
                                       boolean autoHide,
                                       boolean hideRepresentativeButtonOnVisible,
                                       boolean idVisibleOnTitleBar,
                                       int minimumDockLength) {
        super(toolWindowDescriptor, parent, true, animating, autoHide, idVisibleOnTitleBar, hideRepresentativeButtonOnVisible);
        
        this.toolsMenu = new JMenu(SwingUtil.getString("@@tool.toolsMenu"));

        this.popupMenuEnabled = popupMenuEnabled;

        this.dockLength = dockLength;
        this.minimumDockLength = minimumDockLength;

        this.toolWindowActionHandler = toolWindowActionHandler;
    }


    public ToolWindowType getType() {
        return ToolWindowType.DOCKED;
    }

    public void setPopupMenuEnabled(boolean enabled) {
        if (this.popupMenuEnabled == enabled)
            return;

        boolean old = this.popupMenuEnabled;
        this.popupMenuEnabled = enabled;

        firePropertyChangeEvent("popupMenuEnabled", old, enabled);
    }

    public boolean isPopupMenuEnabled() {
        return popupMenuEnabled;
    }

    public JMenu getToolsMenu() {
        return toolsMenu;
    }

    public int getDockLength() {
        return dockLength;
    }

    public void setDockLength(int dockLength) {
        if (this.dockLength == dockLength || dockLength <= 0)
            return;

        if (dockLength < minimumDockLength)
            setMinimumDockLength(dockLength);

        int old = this.dockLength;
        this.dockLength = dockLength;

        firePropertyChangeEvent("dockLength", old, dockLength);
    }

    public void setMinimumDockLength(int minimumDockLength) {
        if (this.minimumDockLength == minimumDockLength)
            return;

        int old = this.minimumDockLength;
        this.minimumDockLength = minimumDockLength;
        
        firePropertyChangeEvent("minimumDockLength", old, minimumDockLength);
    }

    public int getMinimumDockLength() {
        return minimumDockLength;
    }

    public ToolWindowActionHandler getToolWindowActionHandler() {
        return toolWindowActionHandler;
    }

    public void setToolWindowActionHandler(ToolWindowActionHandler toolWindowActionHandler) {
        ToolWindowActionHandler old = this.toolWindowActionHandler;
        this.toolWindowActionHandler = toolWindowActionHandler;

        firePropertyChangeEvent("toolWindowActionHandler", old, toolWindowActionHandler);
    }

    public void setEnabled(boolean enabled) {
        throw new RuntimeException("Cannot call this method. This type is always available.");
    }


    public ToolWindowTypeDescriptor cloneMe(ToolWindowDescriptor toolWindowDescriptor) {
        return new DefaultDockedTypeDescriptor(toolWindowDescriptor,
                                               this,
                                               dockLength,
                                               popupMenuEnabled,
                                               toolWindowActionHandler,
                                               animating,
                                               autoHide,
                                               hideRepresentativeButtonOnVisible,
                                               idVisibleOnTitleBar,
                                               minimumDockLength
        );
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);

        if ("popupMenuEnabled".equals(evt.getPropertyName())) {
            setPopupMenuEnabled((Boolean) evt.getNewValue());
        } else if ("dockLength".equals(evt.getPropertyName())) {
            setDockLength((Integer) evt.getNewValue());
        } else if ("toolWindowActionHandler".equals(evt.getPropertyName())) {
            setToolWindowActionHandler((ToolWindowActionHandler) evt.getNewValue());
        }
    }

}
