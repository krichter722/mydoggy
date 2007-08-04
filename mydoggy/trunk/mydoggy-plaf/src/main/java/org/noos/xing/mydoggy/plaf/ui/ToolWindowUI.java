package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowUI {

    public static String DOCKED = "DOCKED";
    public static String DOCKED_INACTIVE = "DOCKED_INACTIVE";
    public static String SLIDING = "SLIDING";
    public static String SLIDING_INACTIVE = "SLIDING_INACTIVE";
    public static String FLOATING = "FLOATING";
    public static String FLOATING_INACTIVE = "FLOATING_INACTIVE";
    public static String FIX = "FIX";
    public static String FIX_INACTIVE = "FIX_INACTIVE";
    public static String AUTO_HIDE_ON = "AUTO_HIDE_ON";
    public static String AUTO_HIDE_ON_INACTIVE = "AUTO_HIDE_ON_INACTIVE";
    public static String AUTO_HIDE_OFF = "AUTO_HIDE_OFF";
    public static String AUTO_HIDE_OFF_INACTIVE = "AUTO_HIDE_OFF_INACTIVE";
    public static String HIDE_TOOL_WINDOW = "HIDE_TOOL_WINDOW";
    public static String HIDE_TOOL_WINDOW_INACTIVE = "HIDE_TOOL_WINDOW_INACTIVE";
    public static String MAXIMIZE = "MAXIMIZE";
    public static String MAXIMIZE_INACTIVE = "MAXIMIZE_INACTIVE";
    public static String MINIMIZE = "MINIMIZE";
    public static String MINIMIZE_INACTIVE = "MINIMIZE_INACTIVE";

    public static String ANCHOR_LINE_BORDER_VISIBLE = "ANCHOR_LINE_BORDER_VISIBLE";
    public static String ANCHOR_LINE_BORDER_NOT_VISIBLE = "ANCHOR_LINE_BORDER_NOT_VISIBLE"; 

    Icon getIcon(String id);

    Color getColor(String id);


    void updateAnchor(boolean opaque, Graphics g, Rectangle rectangle, Color start, Color end);

    void updateAnchorFlash(Graphics g, Rectangle rectangle, Color flashingAnimBackStart, Color flashingAnimBackEnd);

}
