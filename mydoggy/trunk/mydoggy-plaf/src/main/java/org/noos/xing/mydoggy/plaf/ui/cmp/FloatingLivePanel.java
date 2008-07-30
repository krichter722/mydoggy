package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.FloatingLiveTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingResizeMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowFloatingLiveDropTarget;
import org.noos.xing.mydoggy.plaf.ui.translucent.TranslucentPanel;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingLivePanel extends TranslucentPanel implements PropertyChangeListener,
                                                                   ActionListener {

    protected MyDoggyToolWindowManager manager;
    protected JLayeredPane layeredPane;

    protected ContentPanel contentPanel;
    protected MultiSplitDockableContainer multiSplitDockableContainer;

    protected TransparencyAnimation animation;
    protected Timer timer;


    public FloatingLivePanel(MyDoggyToolWindowManager manager) {
        this.manager = manager;

        initComponents();
        initListeners();
    }


    public void propertyChange(final PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("active".equals(propertyName)) {
            if (Boolean.TRUE.equals(evt.getNewValue())) {
                layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 4);

                if (timer != null) {
                    timer.stop();
                    if (animation.isAnimating())
                        animation.stop();
                }
                setAlphaModeRatio(1.0f);
            } else {
                layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 3);

                FloatingLiveTypeDescriptor floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) ((ToolWindow) evt.getSource()).getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
                if (floatingLiveTypeDescriptor.isTransparentMode()) {
                    timer = new Timer(floatingLiveTypeDescriptor.getTransparentDelay() + 100, this) {
                        @Override
                        protected void fireActionPerformed(ActionEvent e) {
                            e = new ActionEvent(evt.getSource(),
                                                e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
                            super.fireActionPerformed(e);
                        }
                    };
                    timer.start();
                }
            }
            SwingUtil.repaint(layeredPane);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (timer.isRunning()) {
            timer.stop();

            FloatingLiveTypeDescriptor floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) ((ToolWindow) e.getSource()).getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
            animation.setAlpha(floatingLiveTypeDescriptor.getTransparentRatio());
            animation.show();
        }
    }


    public void resetLayout() {
        TableLayout layout = (TableLayout) getLayout();
        layout.setColumn(0, 0);
        layout.setColumn(2, 0);
        layout.setRow(0, 0);
        layout.setRow(2, 0);
    }

    public void setLayout() {
        TableLayout layout = (TableLayout) getLayout();
        layout.setColumn(0, 2);
        layout.setColumn(2, 2);
        layout.setRow(0, 2);
        layout.setRow(2, 2);
    }

    public void addDockable(ToolWindow toolWindow,
                            Component content) {
        addDockable(toolWindow, content, null, AggregationPosition.DEFAULT);
    }

    public void addDockable(ToolWindow toolWindow,
                            Component content,
                            ToolWindow aggregationOnDockable,
                            AggregationPosition aggregationPosition) {
        multiSplitDockableContainer.addDockable(toolWindow,
                                                content,
                                                aggregationOnDockable,
                                                -1,
                                                aggregationPosition);
        toolWindow.addPropertyChangeListener(this);
    }

    public void removeDockable(ToolWindow toolWindow) {
        multiSplitDockableContainer.removeDockable(toolWindow);

        toolWindow.removePropertyChangeListener(this);
    }

    public void mount() {
        if (getParent() == layeredPane)
            return;

        layeredPane.remove(this);
        layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 3);
        layeredPane.add(this);
    }

    public void unmount() {
        if (multiSplitDockableContainer.getContentCount() == 0) {
            setLayout();
            layeredPane.remove(this);
            setBorder(null);
        }
        SwingUtil.repaint(layeredPane);
    }

    public ToolWindow getFirstToolWindow() {
        return (ToolWindow) multiSplitDockableContainer.getContents().get(0).dockable;
    }


    protected void initComponents() {
        this.layeredPane = manager.getLayeredPane();

        multiSplitDockableContainer = new MultiSplitDockableContainer(manager, JSplitPane.VERTICAL_SPLIT);

        contentPanel = new ContentPanel("toolWindow.container.");
        contentPanel.setDropTarget(new ToolWindowFloatingLiveDropTarget(this, contentPanel, manager));
        contentPanel.setComponent(multiSplitDockableContainer);

        setLayout(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));
        add(contentPanel, "1,1,FULL,FULL");

        this.animation = new TransparencyAnimation(this, this, 1.0f, 500f);
    }

    protected void initListeners() {
        FloatingResizeMouseInputHandler resizeMouseInputHandler = new FloatingResizeMouseInputHandler(this);

        addMouseMotionListener(resizeMouseInputHandler);
        addMouseListener(resizeMouseInputHandler);
    }

}
