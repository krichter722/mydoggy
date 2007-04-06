package org.noos.xing.mydoggy.plaf.ui.icons;

import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;

public class IconProvider {
    public static final Icon docked;
    public static final Icon dockedInactive;

    public static final Icon sliding;
    public static final Icon slidingInactive;

    public static final Icon floating;
    public static final Icon floatingInactive;

    public static final Icon fix;
    public static final Icon fixInactive;

    public static final Icon autoHideOn;
    public static final Icon autoHideOnInactive;

    public static final Icon autoHideOff;
    public static final Icon autoHideOffInactive;

    public static final Icon hideToolWindow;
    public static final Icon hideToolWindowInactive;

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
