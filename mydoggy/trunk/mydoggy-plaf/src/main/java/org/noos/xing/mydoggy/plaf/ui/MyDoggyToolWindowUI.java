package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;

import javax.swing.*;
import java.util.Hashtable;
import java.util.Map;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowUI implements ToolWindowUI {
    static final Color gray = new Color(247, 243, 239);

    private static Map<String, Icon> staticIcons;
    private static Map<String, Color> staticColors;

    static {
        staticIcons = new Hashtable<String, Icon>();

        staticIcons.put(SLIDING, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/sliding.png"));
        staticIcons.put(SLIDING_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/slidingInactive.png"));

        staticIcons.put(FLOATING, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floating.png"));
        staticIcons.put(FLOATING_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floatingInactive.png"));

        staticIcons.put(FIX, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fix.png"));
        staticIcons.put(FIX_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fixInactive.png"));

        staticIcons.put(DOCKED, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/docked.png"));
        staticIcons.put(DOCKED_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/dockedInactive.png"));

        staticIcons.put(AUTO_HIDE_ON, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOn.png"));
        staticIcons.put(AUTO_HIDE_ON_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOnInactive.png"));

        staticIcons.put(AUTO_HIDE_OFF, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOff.png"));
        staticIcons.put(AUTO_HIDE_OFF_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOffInactive.png"));

        staticIcons.put(HIDE_TOOL_WINDOW, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindow.png"));
        staticIcons.put(HIDE_TOOL_WINDOW_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindowInactive.png"));

        staticIcons.put(MAXIMIZE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/maximize.png"));
        staticIcons.put(MAXIMIZE_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/maximizeInactive.png"));

        staticIcons.put(MINIMIZE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/minimize.png"));
        staticIcons.put(MINIMIZE_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/minimizeInactive.png"));

        staticColors = new Hashtable<String, Color>();

    }

    public MyDoggyToolWindowUI() {
    }

    public Icon getIcon(String id) {
        return staticIcons.get(id);
    }

    public Color getColor(String id) {
        return staticColors.get(id);
    }

    public void updateAnchor(boolean active, Graphics g, Rectangle rectangle, Color start, Color end) {
        if (active) {
            GraphicsUtil.fillRect(g, rectangle,
                                  start, end, null, GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
        } else {
            g.setColor(gray);
            g.fillRect(0, 0, rectangle.width, rectangle.height);
        }
    }

    public void updateAnchorFlash(Graphics g, Rectangle rectangle, Color start, Color end) {
        GraphicsUtil.fillRect(g, rectangle, start, end,
                              null, GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
    }

}
