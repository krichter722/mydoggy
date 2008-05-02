package org.noos.xing.mydoggy.mydoggyset.view.group.model;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowManagerListener;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsComboBoxModel extends DefaultComboBoxModel {

    public ToolsComboBoxModel(ViewContext viewContext) {
        viewContext.get(ToolWindowManager.class).addToolWindowManagerListener(new ToolWindowManagerListener() {
            public void toolWindowRegistered(ToolWindowManagerEvent event) {
                addElement(event.getToolWindow().getId());
                fireContentsChanged(this, getSize() - 2, getSize() - 1);
            }

            public void toolWindowUnregistered(ToolWindowManagerEvent event) {
            }

            public void toolWindowGroupAdded(ToolWindowManagerEvent event) {
            }

            public void toolWindowGroupRemoved(ToolWindowManagerEvent event) {
            }
        });
    }
}
