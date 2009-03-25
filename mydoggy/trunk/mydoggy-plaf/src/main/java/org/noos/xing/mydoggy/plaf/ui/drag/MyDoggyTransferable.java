package org.noos.xing.mydoggy.plaf.ui.drag;

import org.noos.xing.mydoggy.ToolWindowManager;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyTransferable implements Transferable, ClipboardOwner {
    public static final DataFlavor TOOL_WINDOW_MANAGER = new DataFlavor(Integer.class, "toolWindowManager");
    public static final DataFlavor TOOL_WINDOW_ID_DF = new DataFlavor(String.class, "toolWindowId");
    public static final DataFlavor TOOL_WINDOW_TAB_ID_DF = new DataFlavor(String.class, "toolWindowTabId");
    public static final DataFlavor CONTENT_ID_DF = new DataFlavor(String.class, "contentId");
    public static final DataFlavor CUSTOM_DESCRIPTOR_ID = new DataFlavor(String.class, "customDescriptorId");

    protected Map<DataFlavor, Object> map;
    protected List<DataFlavor> supportedFlavors;


    public MyDoggyTransferable(ToolWindowManager toolWindowManager) {
        this.map = new Hashtable<DataFlavor, Object>();
        this.supportedFlavors = new ArrayList<DataFlavor>();

        addEntry(TOOL_WINDOW_MANAGER, System.identityHashCode(toolWindowManager));
    }

    public MyDoggyTransferable(ToolWindowManager toolWindowManager, DataFlavor df, Object value) {
        this(toolWindowManager);
        addEntry(df, value);
    }


    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors.toArray(new DataFlavor[supportedFlavors.size()]);
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return supportedFlavors.contains(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (map.containsKey(flavor)) {
            return map.get(flavor);
        } else
            throw new UnsupportedFlavorException(flavor);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }


    public String toString() {
        return "MyDoggyTransferable{" +
               "map=" + map +
               ", supportedFlavors=" + (supportedFlavors == null ? null : Arrays.asList(supportedFlavors)) +
               '}';
    }


    public void addEntry(DataFlavor dataFlavor, Object value) {
        map.put(dataFlavor, value);
        supportedFlavors.add(dataFlavor);
    }
}