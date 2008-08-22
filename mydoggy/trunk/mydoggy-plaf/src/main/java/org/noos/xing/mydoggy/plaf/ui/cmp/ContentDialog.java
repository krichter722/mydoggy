package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
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
public class ContentDialog extends JDialog implements ContentWindow {
    protected Content content;
    protected ContentUI contentUI;

    protected MultiSplitDockableContainer multiSplitDockableContainer;


    public ContentDialog(Content content, ContentUI contentUI, Frame parentFrame, Rectangle inBounds) throws HeadlessException {
        super(SwingUtil.getBoolean(MyDoggyKeySpace.WINDOW_ALWAYS_ON_TOP, true) ? parentFrame : null, false);

        this.content = content;
        this.contentUI = contentUI;

        installComponents();
        installListeners(parentFrame);

        // Set Bounds...
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


    public void addContent(Content content, Component contentComponent) {
        addContent(content, contentComponent, null, AggregationPosition.DEFAULT);
    }

    public void addContent(Content content,
                           Component componentContent,
                           Content aggregationOnContent,
                           AggregationPosition aggregationPosition) {
        multiSplitDockableContainer.addDockable(content,
                                                componentContent,
                                                aggregationOnContent,
                                                -1,
                                                aggregationPosition);
    }

    public void removeContent(Content content) {
        multiSplitDockableContainer.removeDockable(content);
    }

    public int getNumContents() {
        return multiSplitDockableContainer.getContentCount();
    }

    public boolean containsContent(Content content) {
        return multiSplitDockableContainer.containsDockable(content);
    }


    protected void installComponents() {
        // Setup title
        setTitle(content.getTitle());

        // Add content
        Component component = content.getComponent();
        component.setPreferredSize(component.getSize());

        multiSplitDockableContainer = new ContentWindowMultiSplitContainer(
                (MyDoggyToolWindowManager) content.getDockableManager().getToolWindowManager()
        );
        multiSplitDockableContainer.addDockable(content, component, null);

        setLayout(new ExtendedTableLayout(new double[][]{{0, TableLayout.FILL, 0}, {0, TableLayout.FILL, 0}}));
        add(multiSplitDockableContainer, "1,1,FULL,FULL");
    }

    protected void installListeners(Frame parentFrame) {
        // Init Listener
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new ContentDialogWindowAdapter());
        if (parentFrame == null)
            addWindowFocusListener(new ToFrontWindowFocusListener(this));

        addComponentListener(new ContentDialogComponentAdapter());

        if (SwingUtil.getTransparencyManager().isServiceAvailable()) {
            WindowTransparencyListener windowTransparencyListener = new WindowTransparencyListener(
                    SwingUtil.getTransparencyManager(),
                    contentUI,
                    this
            );
            addWindowListener(windowTransparencyListener);
            addWindowFocusListener(windowTransparencyListener);
        }
    }


    public class ContentDialogWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            content.setDetached(false);
        }
    }

    public class ContentDialogComponentAdapter extends ComponentAdapter {

        public void componentResized(ComponentEvent e) {
            if (isActive() && isVisible())
                contentUI.setDetachedBounds(getBounds());
        }

        public void componentMoved(ComponentEvent e) {
            if (isActive() && isVisible())
                contentUI.setDetachedBounds(getBounds());
        }

    }

}
