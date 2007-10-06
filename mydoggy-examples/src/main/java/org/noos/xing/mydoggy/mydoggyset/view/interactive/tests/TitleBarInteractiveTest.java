package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.itest.InteractiveUI;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.look.MyDoggyResourceManager;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleBarUI;

import javax.swing.plaf.ComponentUI;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TitleBarInteractiveTest extends AbstractInteractiveTest {
    protected ToolWindowManager toolWindowManager;

    public TitleBarInteractiveTest(ToolWindowManager toolWindowManager, Container masterContainer) {
        super(masterContainer);
        this.toolWindowManager = toolWindowManager;
    }

    public String getDescription() {
        return "TitleBarInteractiveTest";
    }

    public void execute(InteractiveUI interactiveUI) {
        toolWindowManager.getToolWindowGroup().setVisible(false);

        MyDoggyToolWindowManager manager = (MyDoggyToolWindowManager) toolWindowManager;

        // Customize toolwindow TitleBar ...
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_START, Color.RED);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_END, Color.PINK);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_START, Color.BLACK);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_END, Color.GRAY);

        // Customize toolwindow tabs ...
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED, Color.BLUE);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED, Color.CYAN);

        clickOn(interactiveUI, "toolWindow.rb.Tool 1");
    }


}