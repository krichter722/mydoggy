package org.noos.xing.mydoggy.plaf.ui.cmp.drag;

import org.noos.xing.mydoggy.ToolWindow;

import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTrasferable implements Transferable, ClipboardOwner {
    public static final DataFlavor TOOL_WINDOW_DATA_FAVLOR = new DataFlavor(
            ToolWindow.class, "ToolWindow"
    );

    private static final DataFlavor[] flavors = {TOOL_WINDOW_DATA_FAVLOR};

    private ToolWindow toolWindow;

    public ToolWindowTrasferable(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors.clone();
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor flavor1 : flavors) {
            if (flavor.equals(flavor1)) {
                return true;
            }
        }
        return false;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(TOOL_WINDOW_DATA_FAVLOR)) {
            return toolWindow;
        } else
            throw new UnsupportedFlavorException(flavor);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
