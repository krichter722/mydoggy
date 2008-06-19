package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MyDoggyUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabButtonUI extends BasicPanelUI implements Cleaner, PropertyChangeListener {
    protected ToolWindowManager manager;
    protected ToolWindow toolWindow;
    protected MyDoggyToolWindowTab tab;

    protected Component tabButton;

    protected Timer flashingTimer;
    protected int flasingDuration = -1;
    protected boolean flashingState;


    public ToolWindowTabButtonUI(MyDoggyToolWindowManager manager, MyDoggyToolWindowTab tab) {
        this.manager = manager;
        this.tab = tab;
        this.toolWindow = tab.getOwner();

        tab.getCleaner().addCleaner(this);
    }


    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        this.tabButton = c;
    }

    public void update(Graphics g, JComponent c) {
        if (tab == null || toolWindow == null)
            return;

        Rectangle bounds = c.getBounds();
        bounds.x = bounds.y = 0;

        if (tab.isFlashing() && toolWindow.isVisible()) {
            if (flashingState) {
                GraphicsUtil.fillRect(g, bounds,
                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                      new RoundRectangle2D.Double(
                                              bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10
                                      ),
                                      GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
            } else {
                if (toolWindow.isActive()) {
                    GraphicsUtil.fillRect(g, bounds,
                                          UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                          UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                          new RoundRectangle2D.Double(
                                                  bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10
                                          ),
                                          GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
                }
            }

            if (flashingTimer == null) {
                flashingTimer = new Timer(600, new ActionListener() {
                    long start = 0;

                    public void actionPerformed(ActionEvent e) {
                        Rectangle bounds = tabButton.getBounds();
                        bounds.x = bounds.y = 0;

                        if (start == 0)
                            start = System.currentTimeMillis();

                        flashingState = !flashingState;

                        SwingUtil.repaint(tabButton);

                        if (flasingDuration != -1 && System.currentTimeMillis() - start > flasingDuration)
                            tab.setFlashing(false);
                    }
                });

                flashingState = true;
            }

            if (!flashingTimer.isRunning()) {
                flashingTimer.start();
            }
        } else if (MyDoggyUtil.getNumTabs(toolWindow) > 1) {
            if (tab.isSelected()) {
                if (toolWindow.isActive()) {
                    GraphicsUtil.fillRect(g, bounds,
                                          UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                          UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                          new RoundRectangle2D.Double(
                                                  bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10
                                          ),
                                          GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
                }
            }
        }

        super.update(g, c);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        if ("flash".equals(property)) {
            if (evt.getNewValue() == Boolean.TRUE) {
                if (!tab.isSelected()) {
                    flasingDuration = -1;
                    SwingUtil.repaint(tabButton);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(tabButton);
                }
            }
        } else if ("flash.duration".equals(property)) {
            if (evt.getNewValue() == Boolean.TRUE) {
                if (!tab.isSelected()) {
                    flasingDuration = (Integer) evt.getNewValue();
                    SwingUtil.repaint(tabButton);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(tabButton);
                }
            }
        }
    }

    public void cleanup() {
        if (flashingTimer != null)
            flashingTimer.stop();
        flashingTimer = null;

        manager = null;
        tab = null;
        toolWindow = null;
    }
    
}
