package org.noos.xing.mydoggy.plaf.ui.cmp.drag;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;

import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentTrasferable implements Transferable, ClipboardOwner {
    public static final DataFlavor CONTENT_DATA_FAVLOR = new DataFlavor(
            Content.class, "Content"
    );

    private static final DataFlavor[] flavors = {CONTENT_DATA_FAVLOR};

    private Content content;

    public ContentTrasferable(Content content) {
        this.content = content;
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
        if (flavor.equals(CONTENT_DATA_FAVLOR)) {
            return content;
        } else
            throw new UnsupportedFlavorException(flavor);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }


    public String toString() {
        return "ContentTrasferable{" +
               "content=" + content +
               '}';
    }
}