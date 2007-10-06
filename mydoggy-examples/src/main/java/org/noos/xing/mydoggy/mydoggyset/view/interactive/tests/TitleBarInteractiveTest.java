package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.InteractiveUI;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;

import java.awt.*;

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
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START, Color.RED);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END, Color.PINK);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START, Color.BLACK);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END, Color.GRAY);

        // Customize toolwindow tabs ...
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED, Color.BLUE);
        manager.getResourceManager().putColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED, Color.CYAN);

        clickOn(interactiveUI, "toolWindow.rb.Tool 1");
    }


}