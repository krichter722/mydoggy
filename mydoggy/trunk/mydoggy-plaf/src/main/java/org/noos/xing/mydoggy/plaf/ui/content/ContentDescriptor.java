package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.AggregateIcon;
import org.noos.xing.mydoggy.plaf.ui.cmp.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.look.ContentRepresentativeAnchorUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentDescriptor implements DockableDescriptor {
    protected MyDoggyToolWindowManager manager;
    protected Content content;

    protected ToolWindowAnchor anchor;
    protected JLabel representativeAnchor;
    protected int anchorIndex;


    public ContentDescriptor(MyDoggyToolWindowManager manager, Content content) {
        this.manager = manager;
        this.content = content;
        this.anchor = ToolWindowAnchor.LEFT;
    }


    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public DockableType getDockableType() {
        return DockableType.CONTENT;
    }

    public Dockable getDockable() {
        return content;
    }

    public JComponent getRepresentativeAnchor(Component container) {
        if (representativeAnchor == null) {
            ToolWindowAnchor anchor = getAnchor();

            String labelText = getResourceManager().getUserString(content.getId());
            String toolRepresentativeAnchorText = labelText;
            Icon toolIcon = content.getIcon();

            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeAnchor = new RepresentativeAnchor(toolRepresentativeAnchorText, toolIcon, JLabel.CENTER);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(container, toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new RepresentativeAnchor(compositeIcon, JLabel.CENTER);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(container, toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new RepresentativeAnchor(compositeIcon, JLabel.CENTER);
                    break;
            }

            representativeAnchor.setName("toolWindow.rb." + content.getId());
            representativeAnchor.setOpaque(true);
            representativeAnchor.setFocusable(false);
            representativeAnchor.putClientProperty(DockableDescriptor.class, this);
        }

        return representativeAnchor;
    }

    public JComponent getRepresentativeAnchor() {
        return representativeAnchor;
    }

    public void resetRepresentativeAnchor() {
        representativeAnchor = null;
    }

    public int getRepresentativeAnchorIndex() {
        if (representativeAnchor == null)
            return -1;
        
        return getToolBar().getRepresentativeAnchorIndex(representativeAnchor);
    }

    public void updateRepresentativeAnchor() {
        if (representativeAnchor != null) {
            ToolWindowAnchor anchor = getAnchor();

            String labelText = getResourceManager().getUserString(content.getId());
            String toolRepresentativeAnchorText = labelText;
            Icon toolIcon = content.getIcon();

            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeAnchor.setIcon(toolIcon);
                    representativeAnchor.setText(toolRepresentativeAnchorText);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getLeftIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor.setText(null);
                    representativeAnchor.setIcon(compositeIcon);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getRightIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeAnchor.setText(null);
                    representativeAnchor.setIcon(compositeIcon);
                    break;
            }
        }
    }


    public ResourceManager getResourceManager() {
        return manager.getResourceManager();
    }

    public MyDoggyToolWindowManager getManager() {
        return manager;
    }

    public MyDoggyToolWindowBar getToolBar() {
        return manager.getBar(getAnchor());
    }

    public boolean isPreviewAvailable() {
        return false;
    }

    public Component getPreviewComponent() {
        return null;
    }

    public void setAnchor(ToolWindowAnchor anchor, int index) {
        manager.propertyChange(
                new UserPropertyChangeEvent(this, "available", true, false,
                                            new Object[]{-1, false}
                )
        );

        this.anchor = anchor;
        this.anchorIndex = index;

        manager.propertyChange(
                new UserPropertyChangeEvent(this, "available", false, true,
                                            new Object[]{anchorIndex, false}
                )
        );
    }


    public class RepresentativeAnchor extends JLabel {

        public RepresentativeAnchor(Icon image, int horizontalAlignment) {
            super(image, horizontalAlignment);
            super.setUI((LabelUI) createRepresentativeAnchorUI());
        }

        public RepresentativeAnchor(String text, Icon icon, int horizontalAlignment) {
            super(text, icon, horizontalAlignment);
            super.setUI((LabelUI) createRepresentativeAnchorUI());
        }

        public void setUI(LabelUI ui) {
        }

        public void updateUI() {
            firePropertyChange("UI", null, getUI());
        }

        protected ComponentUI createRepresentativeAnchorUI() {
            return new ContentRepresentativeAnchorUI(ContentDescriptor.this);
        }
    }

}
