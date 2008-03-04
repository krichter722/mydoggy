package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.content.PlafContent;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentFrame extends JFrame {
    protected PlafContent content;
    protected ContentUI contentUI;

    public ContentFrame(ResourceManager resourceManager,
                         PlafContent content, ContentUI contentUI,
                         Frame parentFrame) throws HeadlessException {
        setAlwaysOnTop(resourceManager.getBoolean("dialog.owner.enabled", true));
/*
        setFocusCycleRoot(true);
        setFocusTraversalPolicyProvider(true);
        setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());
*/

        this.content = content;
        this.contentUI = contentUI;

        // Setup title and component
        Component component = content.getComponent();
        component.setPreferredSize(component.getSize());

        setTitle(content.getTitle());
        getContentPane().setLayout(new TableLayout(new double[][]{{-1},{-1}}));
        getContentPane().add(component, "0,0,FULL,FULL");

        // Init Listener
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new ContentDialogWindowAdapter());
        if (parentFrame == null)
            addWindowFocusListener(new ToFrontWindowFocusListener(this));

        addComponentListener(new ContentDialogComponentAdapter());

        if (resourceManager.getTransparencyManager().isServiceAvailable()) {
            WindowTransparencyListener windowTransparencyListener = new WindowTransparencyListener(
                    resourceManager.getTransparencyManager(),
                    contentUI,
                    this
            );
            addWindowListener(windowTransparencyListener);
            addWindowFocusListener(windowTransparencyListener);
        }

        // Setup bounds
        Rectangle detachedBounds = SwingUtil.validateWindowBounds(contentUI.getDetachedBounds());
        if (detachedBounds != null) {
            setBounds(detachedBounds);
        } else {
            if (parentFrame != null) {
                Point location = parentFrame.getLocation();
                location.translate(5, 5);
                setLocation(location);
            } else {
                SwingUtil.centrePositionOnScreen(this);
            }
            pack();
        }
    }

    protected class ContentDialogWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            content.setDetached(false);
        }
    }

    protected class ContentDialogComponentAdapter extends ComponentAdapter {

        public void componentResized(ComponentEvent e) {
            contentUI.setDetachedBounds(getBounds());
        }

        public void componentMoved(ComponentEvent e) {
            contentUI.setDetachedBounds(getBounds());
        }
    }
}