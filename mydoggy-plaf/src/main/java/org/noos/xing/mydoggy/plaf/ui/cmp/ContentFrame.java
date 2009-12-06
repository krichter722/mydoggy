package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.multisplit.MultiSplitLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentFrame extends JFrame implements ContentWindow {
    protected Content content;
    protected ContentUI contentUI;

    protected MultiSplitDockableContainer<Content> multiSplitDockableContainer;


    public ContentFrame(Content content, ContentUI contentUI, Frame parentFrame, Rectangle inBounds) throws HeadlessException {
        setAlwaysOnTop(contentUI.isAlwaysOnTop());

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
                    setSize(320, 200);
                } else {
                    SwingUtil.centrePositionOnScreen(this);
                }
            }
        }
    }



    public void addDockable(Content content, Component contentComponent) {
        addDockable(content, contentComponent, null, AggregationPosition.DEFAULT);
    }

    public void addDockable(Content content,
                           Component componentContent,
                           Content aggregationOnContent,
                           AggregationPosition aggregationPosition) {
        multiSplitDockableContainer.addDockable(content,
                                                componentContent,
                                                aggregationOnContent,
                                                -1,
                                                aggregationPosition);
    }

    public void removeDockable(Content content) {
        multiSplitDockableContainer.removeDockable(content);
    }

    public int getDockableCount() {
        return multiSplitDockableContainer.getDockableCount();
    }

    public Content getDockable() {
        return (Content) multiSplitDockableContainer.getDockable();
    }

    public boolean containsDockable(Content content) {
        return multiSplitDockableContainer.containsDockable(content);
    }

    public Object getMultiSplitLayout() {
        return multiSplitDockableContainer.getMultiSplitLayout();
    }

    public List<Content> getDockables() {
        return multiSplitDockableContainer.getDockables();
    }

    public void setMultiSplitLayout(Object model) {
        multiSplitDockableContainer.setMultiSplitLayout((MultiSplitLayout.Node) model);
    }

    public boolean setComponent(Content content, Component component) {
        return multiSplitDockableContainer.setComponent(content, component);
    }

    public boolean isDisposed() {
        return multiSplitDockableContainer == null;
    }

    public void dispose() {
        super.dispose();

        content = null;
        contentUI = null;
        multiSplitDockableContainer = null;
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
        addWindowListener(new ContentWindowAdapter());
        if (parentFrame == null)
            addWindowFocusListener(new ToFrontWindowFocusListener(this));

        addComponentListener(new ContentWindowComponentAdapter());

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


    public class ContentWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            if (multiSplitDockableContainer != null)
                for (Content content : multiSplitDockableContainer.getDockables()) {
                    content.setDetached(false);
                }

            content = null;
            contentUI = null;
            multiSplitDockableContainer = null;
  
            super.windowClosing(event);
        }
    }

    public class ContentWindowComponentAdapter extends ComponentAdapter {


        public void componentResized(ComponentEvent e) {
            update();
        }

        public void componentMoved(ComponentEvent e) {
            update();
        }


        protected void update() {
            if (isActive() && isVisible() && multiSplitDockableContainer != null) {
                Rectangle bounds = getBounds();
                for (Content content : multiSplitDockableContainer.getDockables()) {
                    content.getContentUI().setDetachedBounds(bounds);
                }
            }
        }

    }

}