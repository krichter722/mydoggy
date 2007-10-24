package org.noos.xing.mydoggy.plaf.ui.drag;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyTrasferable implements Transferable, ClipboardOwner {
    public static final DataFlavor TOOL_WINDOW_ID_DF = new DataFlavor("String/toolWindowId", "toolWindowId");
    public static final DataFlavor TOOL_WINDOW_TAB_ID_DF = new DataFlavor("String/toolWindowTabId", "toolWindowTabId");
    public static final DataFlavor CONTENT_ID_DF = new DataFlavor("String/contentId", "contentId");

    protected Map<DataFlavor, Object> map;
    protected List<DataFlavor> supportedFlavors;

    public MyDoggyTrasferable() {
        this.map = new Hashtable<DataFlavor, Object>();
        this.supportedFlavors = new ArrayList<DataFlavor>();
    }

    public MyDoggyTrasferable(DataFlavor df, Object value) {
        this();
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
        return "MyDoggyTrasferable{" +
               "map=" + map +
               ", supportedFlavors=" + (supportedFlavors == null ? null : Arrays.asList(supportedFlavors)) +
               '}';
    }

    public void addEntry(DataFlavor dataFlavor, Object value) {
        map.put(dataFlavor, value);
        supportedFlavors.add(dataFlavor);
    }
}