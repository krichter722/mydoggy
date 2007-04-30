package org.noos.xing.mydoggy.plaf.ui.painter;

import org.noos.xing.mydoggy.ToolWindowUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowUI implements ToolWindowUI {

    private static Map<IconId, Icon> staticIcons;
    static {
        staticIcons = new Hashtable<IconId, Icon>();

        staticIcons.put(IconId.SLIDING, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/sliding.png"));
        staticIcons.put(IconId.SLIDING_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/slidingInactive.png"));

        staticIcons.put(IconId.FLOATING, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floating.png"));
        staticIcons.put(IconId.FLOATING_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floatingInactive.png"));

        staticIcons.put(IconId.FIX, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fix.png"));
        staticIcons.put(IconId.FIX_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fixInactive.png"));

        staticIcons.put(IconId.DOCKED, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/docked.png"));
        staticIcons.put(IconId.DOCKED_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/dockedInactive.png"));

        staticIcons.put(IconId.AUTO_HIDE_ON, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOn.png"));
        staticIcons.put(IconId.AUTO_HIDE_ON_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOnInactive.png"));

        staticIcons.put(IconId.AUTO_HIDE_OFF, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOff.png"));
        staticIcons.put(IconId.AUTO_HIDE_OFF_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOffInactive.png"));

        staticIcons.put(IconId.HIDE_TOOL_WINDOW, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindow.png"));
        staticIcons.put(IconId.HIDE_TOOL_WINDOW_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindowInactive.png"));

        staticIcons.put(IconId.MAXIMIZE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/maximize.png"));
        staticIcons.put(IconId.MAXIMIZE_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/maximizeInactive.png"));

        staticIcons.put(IconId.MINIMIZE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/minimize.png"));
        staticIcons.put(IconId.MINIMIZE_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/minimizeInactive.png"));
    }

    public MyDoggyToolWindowUI() {
    }

    public Icon getIcon(IconId iconId) {
        return staticIcons.get(iconId);
    }

    public void setIcon(IconId iconId, Icon icon) {
        
    }

    public Style getStyle(Target target) {
        return null;
    }

    public void setStyle(Target target, Style style) {
        
    }

    public Color getColor(String key) {
        return null;
    }

    public Color setColor(String key, Color color) {
        return null;
    }

}
