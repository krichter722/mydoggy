package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
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
public class ContentDialog extends JDialog {
    protected Content content;
    protected ContentUI contentUI;


    public ContentDialog(ResourceManager resourceManager,
                         Content content, ContentUI contentUI,
                         Frame parentFrame,
                         Rectangle inBounds) throws HeadlessException {
        super(resourceManager.getBoolean("dialog.owner.enabled", true) ? parentFrame : null, false);
//        setFocusCycleRoot(true);
//        setFocusTraversalPolicyProvider(true);
//        setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());

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
        Rectangle detachedBounds = SwingUtil.validateBounds(contentUI.getDetachedBounds());
        if (detachedBounds != null) {
            setBounds(detachedBounds);
        } else {
            if (inBounds != null) {
                setBounds(inBounds);
            } else {
                if (parentFrame != null) {
                    Point location = parentFrame.getLocation();
                    location.translate(5, 5);
                    setLocation(location);
                } else {
                    SwingUtil.centrePositionOnScreen(this);
                }
            }
        }
    }

    public void dispose() {
        super.dispose();
        
        content = null;
        contentUI = null;
    }

    public void setLocation(int x, int y) {
        super.setLocation(x, y);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setLocation(Point p) {
        super.setLocation(p);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setBounds(Rectangle r) {
        System.out.println("setBounds r = " + r);
        super.setBounds(r);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setSize(Dimension d) {
        super.setSize(d);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected class ContentDialogWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            content.setDetached(false);
        }
    }

    protected class ContentDialogComponentAdapter extends ComponentAdapter {

        public void componentResized(ComponentEvent e) {
            if (isActive() && isVisible()) {
                System.out.println("componentResized getBounds() = " + getBounds());
                contentUI.setDetachedBounds(getBounds());
            }
        }

        public void componentMoved(ComponentEvent e) {
            if (isActive() && isVisible()) {
                System.out.println("componentMoved getBounds() = " + getBounds());
                contentUI.setDetachedBounds(getBounds());
            }
        }

    }
}
