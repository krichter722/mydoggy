package org.noos.xing.mydoggy.plaf.ui.cmp.drag;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UniversalDragGesture implements DragGestureListener, DragSourceMotionListener, DragSourceListener {
    protected UniversalDragCallback universalDragCallback;

    protected Image ghostImage;
    protected JComponent lastOverCmp = null;
    protected Border oldBorder = null;

    protected LineBorder highligthBorder = new LineBorder(Color.BLUE, 3);

    protected boolean moveAnchor;
    protected ToolWindowAnchor lastAnchor;

    public UniversalDragGesture(UniversalDragCallback universalDragCallback) {
        this.universalDragCallback = universalDragCallback;
    }

    public void dragGestureRecognized(DragGestureEvent dge) {
        if (!universalDragCallback.accept(dge))
            return;

        if (DragAndDropLock.isLocked()) {
            DragAndDropLock.setDragAndDropStarted(false);
            return;
        }
        DragAndDropLock.setLocked(true);
        DragAndDropLock.setDragAndDropStarted(true);

        // Start Drag
//            System.out.println("dge.getComponent() = " + dge.getComponent());

        universalDragCallback.startDrag(dge, this);

        // Prepare glassPane for ghost image
        GlassPanel glassPane = universalDragCallback.getGlassPanel();
        glassPane.setVisible(true);

        // Build orginalDragImage
        ghostImage = universalDragCallback.getGhostImage();

        // Setup glasspane
        Point p = (Point) dge.getDragOrigin().clone();
        SwingUtilities.convertPointFromScreen(p, glassPane);
        glassPane.setPoint(p);
        glassPane.setDraggingImage(ghostImage);
        glassPane.repaint();

        lastAnchor = null;
    }

    public void dragMouseMoved(DragSourceDragEvent dsde) {
        if (!DragAndDropLock.isDragAndDropStarted() || ghostImage == null)
            return;

        GlassPanel glassPane = universalDragCallback.getGlassPanel();

        Point p = dsde.getLocation();
        SwingUtilities.convertPointFromScreen(p, glassPane);
        glassPane.setPoint(p);

        Container contentPane = universalDragCallback.getManager().getRootPane().getLayeredPane();
        Component deepestCmp = SwingUtilities.getDeepestComponentAt(contentPane, p.x, p.y);

        if (deepestCmp != null) {
            if (deepestCmp instanceof ToolWindowTabPanel ||
                SwingUtil.getParent(deepestCmp, ToolWindowTabPanel.class) != null) {
                moveAnchor = false;

                if (lastOverCmp != null)
                    lastOverCmp.setBorder(oldBorder);

                lastOverCmp = (JComponent) deepestCmp;
                oldBorder = lastOverCmp.getBorder();
                lastOverCmp.setBorder(highligthBorder);
            } else {
                moveAnchor = true;
                if (lastOverCmp != null)
                    lastOverCmp.setBorder(oldBorder);

                lastOverCmp = (JComponent) SwingUtil.getParent(deepestCmp, "toolWindow.container");
                if (lastOverCmp != null) {
                    oldBorder = lastOverCmp.getBorder();
                    lastOverCmp.setBorder(highligthBorder);
                } else {
                    lastOverCmp = (JComponent) SwingUtil.getParent(deepestCmp, "toolWindowManager.mainContainer");
                    if (lastOverCmp != null) {
                        oldBorder = lastOverCmp.getBorder();
                        lastOverCmp.setBorder(highligthBorder);
                        moveAnchor = false;
                    }
                }
            }
        }

        p = dsde.getLocation();
        SwingUtilities.convertPointFromScreen(p, universalDragCallback.getManager());
        ToolWindowAnchor newAnchor = universalDragCallback.getManager().getToolWindowAnchor(p);

        if (newAnchor != lastAnchor) {
            Rectangle dirtyRegion = glassPane.getRepaintRect();

            if (newAnchor == null) {
                universalDragCallback.getManager().getBar(lastAnchor).setTempShowed(false);
            } else {
                if (universalDragCallback.getManager().getBar(newAnchor).getAvailableTools() == 0)
                    universalDragCallback.getManager().getBar(newAnchor).setTempShowed(true);
            }

            lastAnchor = newAnchor;
            glassPane.repaint(dirtyRegion);
        }

        glassPane.repaint(glassPane.getRepaintRect());
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (!DragAndDropLock.isDragAndDropStarted() || ghostImage == null)
            return;

        DragAndDropLock.setDragAndDropStarted(false);

        Transferable transferable = dsde.getDragSourceContext().getTransferable();
        if (transferable.isDataFlavorSupported(ToolWindowTrasferable.TOOL_WINDOW_DATA_FAVLOR)) {
            // Do action
            // TODO: choose during moving the action type...
            if (lastOverCmp != null) {
                // Clear border
                lastOverCmp.setBorder(oldBorder);

                if (moveAnchor) {
                    // Move tool to another anchor
                    ToolWindow destToolWindow = (ToolWindow) lastOverCmp.getClientProperty(ToolWindow.class);
                    assert destToolWindow != null;

                    ToolWindowAnchor anchor = destToolWindow.getAnchor();

                    ToolWindow toolWindow = universalDragCallback.getToolWindow();
                    boolean oldAM = toolWindow.isAggregateMode();
                    try {
                        toolWindow.setAggregateMode(true);
                        toolWindow.setAnchor(anchor,
                                             ((MyDoggyToolWindow) destToolWindow).getDescriptor().getRepresentativeAnchorIndex());
                    } finally {
                        toolWindow.setAggregateMode(oldAM);
                    }
                } else {
                    JComponent targetContainer = (JComponent) SwingUtil.getParent(lastOverCmp, "toolWindow.container");
                    if (targetContainer != null)  {
                        ToolWindow toolWindow = universalDragCallback.getToolWindow();
                        // Add the tool as tab
                        ToolWindow target = (ToolWindow) ((JComponent) SwingUtil.getParent(lastOverCmp, "toolWindow.container")).getClientProperty(
                                ToolWindow.class
                        );
                        if (target != toolWindow)
                            target.addToolWindowTab(toolWindow).setSelected(true);
                    } else  {
                        // Ad as content

                        // TODO: if a toolwindow has tab???
                        ToolWindow toolWindow = universalDragCallback.getToolWindow();
                        universalDragCallback.getManager().getContentManager().addContent(
                                toolWindow
                        );
                    }
                }
            }
        } else if (transferable.isDataFlavorSupported(ToolWindowTabTrasferable.TOOL_WINDOW_TAB_DATA_FAVLOR)) {
            if (lastOverCmp != null) {
                // Clear border
                lastOverCmp.setBorder(oldBorder);

                try {
                    ToolWindow toolWindow = universalDragCallback.getToolWindow();
                    MyDoggyToolWindowTab toolWindowTab = (MyDoggyToolWindowTab) transferable.getTransferData(ToolWindowTabTrasferable.TOOL_WINDOW_TAB_DATA_FAVLOR);
                    toolWindow.removeToolWindowTab(toolWindowTab);

                    toolWindow = toolWindowTab.getToolWindow();
                    if (moveAnchor) {
                        // Move tool to another anchor
                        ToolWindow destToolWindow = (ToolWindow) lastOverCmp.getClientProperty(ToolWindow.class);
                        assert destToolWindow != null;

                        ToolWindowAnchor anchor = destToolWindow.getAnchor();

                        boolean oldAM = toolWindow.isAggregateMode();
                        try {
                            toolWindow.setAggregateMode(true);
                            toolWindow.setAnchor(anchor,
                                                 ((MyDoggyToolWindow) destToolWindow).getDescriptor().getRepresentativeAnchorIndex());
                            toolWindow.aggregate();
                            toolWindow.setActive(true);
                        } finally {
                            toolWindow.setAggregateMode(oldAM);
                        }
                    } else {
                        JComponent targetContainer = (JComponent) SwingUtil.getParent(lastOverCmp, "toolWindow.container");
                        if (targetContainer != null)  {
                            // Add the tool as tab
                            ToolWindow target = (ToolWindow) ((JComponent) SwingUtil.getParent(lastOverCmp, "toolWindow.container")).getClientProperty(
                                    ToolWindow.class
                            );
                            if (target != toolWindow)
                                target.addToolWindowTab(toolWindow).setSelected(true);
                        } else  {
                            // Ad as content
                            universalDragCallback.getManager().getContentManager().addContent(
                                    toolWindow
                            );
                        }
                    }
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (transferable.isDataFlavorSupported(ContentTrasferable.CONTENT_DATA_FAVLOR)) {
            try {
                Content content = (Content) transferable.getTransferData(ContentTrasferable.CONTENT_DATA_FAVLOR);
                universalDragCallback.getManager().getContentManager().removeContent(content);

                if (content.getToolWindow() != null)  {
                    content.getToolWindow().setType(ToolWindowType.DOCKED);
                    content.getToolWindow().setActive(true);
                } else {
                }
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        // Finalize drag action...
        GlassPanel glassPane = universalDragCallback.getManager().getGlassPanel();

        Point p = (Point) dsde.getLocation().clone();
        SwingUtilities.convertPointFromScreen(p, glassPane);

        glassPane.setDraggingImage(null);
        glassPane.setVisible(false);

        ghostImage = null;

        DragAndDropLock.setLocked(false);
        SwingUtilities.getWindowAncestor(universalDragCallback.getManager()).repaint();
    }

}
