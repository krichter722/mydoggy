package org.noos.xing.mydoggy.plaf.ui.icons;

import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;

public class IconProvider {
    public static Icon docked;
    public static Icon dockedInactive;

    public static Icon sliding;
    public static Icon slidingInactive;

    public static Icon floating;
    public static Icon floatingInactive;

    public static Icon fix;
    public static Icon fixInactive;

    public static Icon autoHideOn;
    public static Icon autoHideOnInactive;

    public static Icon autoHideOff;
    public static Icon autoHideOffInactive;

    public static Icon hideToolWindow;
    public static Icon hideToolWindowInactive;

    static {
        sliding = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/sliding.png");
        slidingInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/slidingInactive.png");

        floating = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floating.png");
        floatingInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/floatingInactive.png");

        fix = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fix.png");
        fixInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/fixInactive.png");

        docked = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/docked.png");
        dockedInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/dockedInactive.png");

        autoHideOn = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOn.png");
        autoHideOnInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOnInactive.png");

        autoHideOff = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOff.png");
        autoHideOffInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/autohideOffInactive.png");

        hideToolWindow = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindow.png");
        hideToolWindowInactive = SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/hideToolWindowInactive.png");
    }

    private IconProvider() {
    }
}
