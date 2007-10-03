package org.noos.xing.mydoggy.plaf.ui.cmp.drag;

import org.noos.xing.mydoggy.ToolWindowTab;

import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabTrasferable implements Transferable, ClipboardOwner {
    public static final DataFlavor TOOL_WINDOW_TAB_DATA_FAVLOR = new DataFlavor(
            ToolWindowTab.class, "ToolWindowTab"
    );

    private static final DataFlavor[] flavors = {TOOL_WINDOW_TAB_DATA_FAVLOR};

    private ToolWindowTab toolWindowTab;

    public ToolWindowTabTrasferable(ToolWindowTab toolWindowTab) {
        this.toolWindowTab = toolWindowTab;
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
        if (flavor.equals(TOOL_WINDOW_TAB_DATA_FAVLOR)) {
            return toolWindowTab;
        } else
            throw new UnsupportedFlavorException(flavor);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}