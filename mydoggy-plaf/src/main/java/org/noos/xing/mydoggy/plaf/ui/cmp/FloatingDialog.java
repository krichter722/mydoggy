package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingResizeMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingDialog extends JDialog implements FloatingWindow,
                                                    PropertyChangeListener,
                                                    ActionListener {
    protected MyDoggyToolWindowManager toolWindowManager;

    // Multi split support
    protected DockableDropPanel dockableDropPanel;
    protected MultiSplitDockableContainer<ToolWindow> multiSplitDockableContainer;

    protected FloatingResizeMouseInputHandler resizeMouseInputHandler; 

    // Transparency support
    protected Timer transparencyTimer;
    protected TransparencyManager<Window> transparencyManager;
    protected TransparencyAnimation transparencyAnimation;


    public FloatingDialog(MyDoggyToolWindowManager toolWindowManager, Window owner, boolean modal) {
        super(owner instanceof Frame ? (Frame) owner : null);

        this.toolWindowManager = toolWindowManager;

        setFocusableWindowState(true);
        setModal(modal);

        enableEvents(WindowEvent.WINDOW_EVENT_MASK | ComponentEvent.MOUSE_MOTION_EVENT_MASK);

        initComponents();
        initListeners();
    }


    public void propertyChange(final PropertyChangeEvent evt) {
        if ("active".equals(evt.getPropertyName())) {
            ToolWindow toolWindow = (ToolWindow) evt.getSource();
            FloatingTypeDescriptor typeDescriptor = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class);

            if (transparencyTimer != null) {
                transparencyTimer.stop();
                if (transparencyAnimation.isAnimating()) {
                    synchronized (transparencyManager) {
                        if (transparencyManager.isAlphaModeEnabled(FloatingDialog.this)) {
                            transparencyAnimation.stop();
                            transparencyManager.setAlphaModeRatio(FloatingDialog.this, 1.0f);
                        }
                    }
                }
            }

            if (typeDescriptor.isTransparentMode()) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    if (transparencyAnimation != null) {
                        transparencyTimer = new Timer(typeDescriptor.getTransparentDelay() + 100, this) {
                            @Override
                            protected void fireActionPerformed(ActionEvent e) {
                                e = new ActionEvent(evt.getSource(),
                                                    e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
                                super.fireActionPerformed(e);
                            }
                        };
                        transparencyTimer.start();
                    }
                } else {
                    if (transparencyTimer != null)
                        transparencyTimer.stop();

                    if (transparencyAnimation != null) {
                        synchronized (transparencyManager) {
                            if (transparencyManager.isAlphaModeEnabled(FloatingDialog.this)) {
                                transparencyAnimation.stop();
                                transparencyManager.setAlphaModeRatio(FloatingDialog.this, 1.0f);
                            }
                        }
                    }
                }
            }
        }
/*
        else if (evt.getPropertyName().startsWith("visible")) {
            synchronized (transparencyManager) {
                if (evt.getNewValue() == Boolean.FALSE && transparencyManager.isAlphaModeEnabled(FloatingDialog.this)) {
                    if (transparencyTimer != null)
                        transparencyTimer.stop();

                    if (transparencyManager.isAlphaModeEnabled(FloatingDialog.this)) {
                        transparencyAnimation.stop();
                        transparencyManager.setAlphaModeRatio(FloatingDialog.this, 0.0f);
                    }
                }
            }

            if (evt.getNewValue() == Boolean.TRUE) {
                ToolWindow toolWindow = (ToolWindow) evt.getSource();
                FloatingTypeDescriptor typeDescriptor = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class);

                if (typeDescriptor.isTransparentMode()) {
                    transparencyTimer = new Timer(1000 + typeDescriptor.getTransparentDelay(), this);
                    transparencyTimer.start();
                }
            }
        }
*/
    }

    public void actionPerformed(ActionEvent e) {
        if (transparencyTimer.isRunning()) {
            transparencyTimer.stop();

            if (transparencyAnimation != null) {
                FloatingTypeDescriptor floatingTypeDescriptor = ((ToolWindow) e.getSource()).getTypeDescriptor(FloatingTypeDescriptor.class);
                transparencyAnimation.setAlpha(floatingTypeDescriptor.getTransparentRatio());
                transparencyAnimation.show();
            }
        }
    }


    public void setVisible(boolean visible) {
        if (!visible) {
            TransparencyManager<Window> transparencyManager = SwingUtil.getTransparencyManager();
            transparencyManager.setAlphaModeRatio(this, 1.0f);
        } 

        super.setVisible(visible);
    }

    public void importFrom(FloatingWindow oldWindow) {
        setName(oldWindow.getName());
        setBounds(oldWindow.getBounds());
        setContentPane(oldWindow.getContentPane());

        Component child = getContentPane().getComponent(0);
        if (child instanceof FloatingWindowDockableDropPanel) {
            FloatingWindowDockableDropPanel floatingWindowDockableDropPanel = (FloatingWindowDockableDropPanel) child;
            floatingWindowDockableDropPanel.setModalWindow(this);

            this.dockableDropPanel = floatingWindowDockableDropPanel;
            this.multiSplitDockableContainer = (MultiSplitDockableContainer<ToolWindow>) dockableDropPanel.getComponent();
        } else
            throw new IllegalArgumentException("Cannot recognize old window.");
    }

    public void setUndecorated(boolean undecorated) {
        super.setUndecorated(undecorated);

        if (getContentPane().getLayout() instanceof TableLayout) {
            if (undecorated) {
                // remove
    //            removeWindowListener(modalWindowListener);

                TableLayout tableLayout = ((TableLayout) getContentPane().getLayout());
                int borderLength = SwingUtil.getInt(MyDoggyKeySpace.MODAL_WINDOW_BORDER_LENGTH, 2);
                tableLayout.setRow(new double[]{borderLength, TableLayout.FILL, borderLength});
                tableLayout.setColumn(new double[]{borderLength, TableLayout.FILL, borderLength});

                SwingUtil.revalidate(this);
            } else {
                // add
    //            addWindowListener(modalWindowListener);

                TableLayout tableLayout = ((TableLayout) getContentPane().getLayout());
                tableLayout.setRow(new double[]{0, TableLayout.FILL, 0});
                tableLayout.setColumn(new double[]{0, TableLayout.FILL, 0});

                SwingUtil.revalidate(this);
            }
        }
    }

    public Window getWindow() {
        return this;
    }

    public void addDockable(ToolWindow toolWindow, Component content) {
        addDockable(toolWindow, content, null, AggregationPosition.DEFAULT);

        if (getDockableCount() == 1)
            setTitle(toolWindow.getTitle());

        // Update 'resizable'
        boolean resizable = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class).isResizable();
        if (getDockableCount() == 1)
            setResizable(resizable);
        else {
            if (resizable)
                setResizable(resizable);
        }
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

        // Update 'resizable'
        boolean resizable = toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class).isResizable();
        if (getDockableCount() == 1)
            setResizable(resizable);
        else {
            if (resizable)
                setResizable(resizable);
        }
    }

    public void removeDockable(ToolWindow toolWindow) {
        try {
            multiSplitDockableContainer.removeDockable(toolWindow);
        } finally {
            toolWindow.removePropertyChangeListener(this);
        }
    }

    public int getDockableCount() {
        return multiSplitDockableContainer.getDockableCount();
    }

    public ToolWindow getDockable() {
        return (ToolWindow) multiSplitDockableContainer.getDockableEntries().get(0).dockable;
    }

    public List<ToolWindow> getDockables() {
        return multiSplitDockableContainer.getDockables();
    }

    public boolean containsDockable(ToolWindow toolWindow) {
        return multiSplitDockableContainer.containsDockable(toolWindow);
    }

    public Object getMultiSplitLayout() {
        return multiSplitDockableContainer.getMultiSplitLayout();
    }

    public void setMultiSplitLayout(Object model) {
        multiSplitDockableContainer.setMultiSplitLayout((MultiSplitLayout.Node) model);
    }

    public void setResizable(boolean resizable) {
        super.setResizable(resizable);
//        this.resizeMouseInputHandler.setResizable(resizable);
    }

    
    protected void initComponents() {
        multiSplitDockableContainer = new MultiSplitDockableContainer<ToolWindow>(toolWindowManager, JSplitPane.VERTICAL_SPLIT);

        dockableDropPanel = new FloatingWindowDockableDropPanel(this, toolWindowManager);
        dockableDropPanel.setComponent(multiSplitDockableContainer);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setLayout(new ExtendedTableLayout(new double[][]{{0, TableLayout.FILL, 0}, {0, TableLayout.FILL, 0}}));
        add(dockableDropPanel, "1,1,FULL,FULL");

        this.transparencyManager = SwingUtil.getTransparencyManager();
        if (transparencyManager.isServiceAvailable()) {
            this.transparencyAnimation = new TransparencyAnimation(SwingUtil.getTransparencyManager(), this, 0.0f);
        } else
            this.transparencyAnimation = null;
    }

    protected void initListeners()  {
        MouseInputListener resizeMouseInputHandler = new FloatingResizeMouseInputHandler(this);

        addMouseListener(resizeMouseInputHandler);
        addMouseMotionListener(resizeMouseInputHandler);

        addWindowListener(new FloatingWindowListener());
    }


    public class FloatingWindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            for (MultiSplitDockableContainer.DockableEntry dockableEntry : multiSplitDockableContainer.getDockableEntries()) {
                ((ToolWindow) dockableEntry.dockable).setVisible(false);
            }

            super.windowClosing(e);
        }

    }
}
