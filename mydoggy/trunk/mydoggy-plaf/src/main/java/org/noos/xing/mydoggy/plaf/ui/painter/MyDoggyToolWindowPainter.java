package org.noos.xing.mydoggy.plaf.ui.painter;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowPainter;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowPainter implements ToolWindowPainter {
    static final Color start = new Color(255, 212, 151);
    static final Color end = new Color(255, 244, 204);
    static final Color gray = new Color(247, 243, 239);

    protected Map<IconId, Icon> icons;

    public MyDoggyToolWindowPainter() {
        initIcons();
    }

    public void updateRepresentativeButton(ToolWindow toolWindow, Rectangle bounds, Graphics g, Status status) {
        switch (status) {
            case ACTIVE:
                GraphicsUtil.fillRect(g, new Rectangle(0, 0, bounds.width, bounds.height),
                                      start, end, null, GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
                break;
            case DE_ACTIVE:
                g.setColor(gray);
                g.fillRect(0, 0, bounds.width, bounds.height);
                break;
        }
    }

    public void updateApplicationBar(ToolWindow toolWindow, Rectangle bounds, Graphics g, Object[] params) {
        GraphicsUtil.fillRect(g, bounds, (Color) params[0], (Color) params[1],
                              null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
    }

    public Icon getIcon(IconId iconId) {
        return icons.get(iconId);
    }

    protected void initIcons() {
        this.icons = new Hashtable<IconId, Icon>();

        icons.put(IconId.SLIDING, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/sliding.png"));
        icons.put(IconId.SLIDING_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/slidingInactive.png"));

        icons.put(IconId.FLOATING, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floating.png"));
        icons.put(IconId.FLOATING_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floatingInactive.png"));

        icons.put(IconId.FIX, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fix.png"));
        icons.put(IconId.FIX_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fixInactive.png"));

        icons.put(IconId.DOCKED, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/docked.png"));
        icons.put(IconId.DOCKED_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/dockedInactive.png"));

        icons.put(IconId.AUTO_HIDE_ON, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOn.png"));
        icons.put(IconId.AUTO_HIDE_ON_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOnInactive.png"));

        icons.put(IconId.AUTO_HIDE_OFF, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOff.png"));
        icons.put(IconId.AUTO_HIDE_OFF_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOffInactive.png"));

        icons.put(IconId.HIDE_TOOL_WINDOW, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindow.png"));
        icons.put(IconId.HIDE_TOOL_WINDOW_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindowInactive.png"));

        icons.put(IconId.MAXIMIZE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/maximize.png"));
        icons.put(IconId.MAXIMIZE_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/maximizeInactive.png"));

        icons.put(IconId.MINIMIZE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/minimize.png"));
        icons.put(IconId.MINIMIZE_INACTIVE, SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/minimizeInactive.png"));        
    }

}
