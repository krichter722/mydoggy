package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 */
public interface ToolWindowPainter {
    enum Status {
        ACTIVE,
        DE_ACTIVE
    }

    enum IconId {
        DOCKED,
        DOCKED_INACTIVE,
        SLIDING,
        SLIDING_INACTIVE,
        FLOATING,
        FLOATING_INACTIVE,
        FIX,
        FIX_INACTIVE,
        AUTO_HIDE_ON,
        AUTO_HIDE_ON_INACTIVE,
        AUTO_HIDE_OFF,
        AUTO_HIDE_OFF_INACTIVE,
        HIDE_TOOL_WINDOW,
        HIDE_TOOL_WINDOW_INACTIVE,
        MAXIMIZE,
        MAXIMIZE_INACTIVE,
        MINIMIZE,
        MINIMIZE_INACTIVE
    }

    void updateRepresentativeButton(ToolWindow toolWindow, Rectangle bounds, Graphics g, Status status);

    void updateApplicationBar(ToolWindow toolWindow, Rectangle bounds, Graphics g, Object[] params);

    Icon getIcon(IconId iconId);

}