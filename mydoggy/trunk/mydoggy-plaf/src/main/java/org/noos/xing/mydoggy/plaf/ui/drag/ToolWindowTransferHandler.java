package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.ToolWindow;

import javax.swing.*;
import java.awt.datatransfer.Transferable;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTransferHandler extends TransferHandler {
    private ToolWindow toolWindow;

    public ToolWindowTransferHandler(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    protected Transferable createTransferable(JComponent c) {
        return new ToolWindowTrasferable(toolWindow);
    }

    public int getSourceActions(JComponent c) {
        return MOVE;
    }

}
