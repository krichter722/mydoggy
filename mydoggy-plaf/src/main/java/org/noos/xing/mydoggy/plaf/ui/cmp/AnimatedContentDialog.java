package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.animation.MoveComponentAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
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
public class AnimatedContentDialog extends JDialog {
    protected Content content;
    protected ContentUI contentUI;

    public AnimatedContentDialog(ResourceManager resourceManager,
                                 Content content, ContentUI contentUI,
                                 Frame parentFrame,
                                 Rectangle bounds) throws HeadlessException {
        super(resourceManager.getBoolean("dialog.owner.enabled", true) ? parentFrame : null, false);
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
        getContentPane().setLayout(new TableLayout(new double[][]{{-1}, {-1}}));
        getContentPane().add(component, "0,0,FULL,FULL");

        // Setup bounds
        setBounds(bounds);

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
    }

    protected class ContentDialogWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            content.setDetached(false);
        }
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            Rectangle detachedBounds = SwingUtil.validateBounds(contentUI.getDetachedBounds());
            if (detachedBounds != null) {
                MoveComponentAnimation animation = new MoveComponentAnimation(200f, this);
                animation.show(detachedBounds);
                setBounds(detachedBounds);
            }
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
