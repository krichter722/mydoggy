package org.noos.xing.mydoggy.mydoggyset.view.interactive;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.mydoggyset.context.MyDoggySetContext;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.yasaf.plaf.view.MapViewContext;
import org.noos.xing.yasaf.view.View;
import org.noos.xing.yasaf.view.ViewContext;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveTestView implements View {
    protected ToolWindowManager toolWindowManager;
    protected JFrame frame;
    protected ViewContext mydoggySetContext;

    public InteractiveTestView(ViewContext mydoggySetContext, JFrame frame, ToolWindowManager toolWindowManager) {
        this.frame = frame;
        this.toolWindowManager = toolWindowManager;
        this.mydoggySetContext = mydoggySetContext;
    }

    public Component getComponent() {
        ViewContext viewContext = new MapViewContext();
        viewContext.put(ToolWindowManager.class, toolWindowManager);
        viewContext.put(JFrame.class, frame);
        viewContext.put(MyDoggySetContext.class, mydoggySetContext);

        JPanel panel = new JPanel();
        panel.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1, 3, -1}}));
        panel.add(new TestChooserView(viewContext).getComponent(), "0,0,FULL,FULL");
        panel.add(new TestRecordingView(viewContext).getComponent(), "0,2,FULL,FULL");

        return panel;
    }

}