package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.CustomDockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.AggregateIcon;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentRepresentativeAnchor;
import org.noos.xing.mydoggy.plaf.ui.cmp.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentDescriptor extends CustomDockableDescriptor implements PropertyChangeListener {
    protected Content content;


    public ContentDescriptor(MyDoggyToolWindowManager manager, Content content) {
        super(manager, ToolWindowAnchor.LEFT, content.getId());
        this.content = content;
        this.anchor = ToolWindowAnchor.LEFT;

        content.addPropertyChangeListener(this);
    }


    public void propertyChange(PropertyChangeEvent evt) {
        updateRepresentativeAnchor();
    }

    public DockableType getDockableType() {
        return DockableType.CONTENT;
    }

    public Dockable getDockable() {
        return content;
    }

    public JComponent getRepresentativeAnchor(Component parent) {
        if (representativeAnchor == null) {
            ToolWindowAnchor anchor = getAnchor();

            String labelText = SwingUtil.getUserString(content.getRepresentativeAnchorDescriptor().getTitle());
            Icon toolIcon = content.getIcon();

            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeAnchor = new ContentRepresentativeAnchor(this, labelText, toolIcon, JLabel.CENTER);
                    break;
                case LEFT:
                    if (SwingUtil.getBoolean(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false) && toolIcon != null)
                        toolIcon = GraphicsUtil.rotate(toolIcon, -Math.PI/2);

                    TextIcon textIcon = new TextIcon(parent, labelText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);

                    representativeAnchor = new ContentRepresentativeAnchor(this, compositeIcon, JLabel.CENTER);
                    break;
                case RIGHT:
                    if (SwingUtil.getBoolean(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false) && toolIcon != null)
                        toolIcon = GraphicsUtil.rotate(toolIcon, Math.PI/2);

                    textIcon = new TextIcon(parent, labelText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);

                    representativeAnchor = new ContentRepresentativeAnchor(this, compositeIcon, JLabel.CENTER);
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
        if (representativeAnchor != null) {
            ((ContentRepresentativeAnchor) representativeAnchor).setUI(null);
            representativeAnchor = null;
        }
    }

    public int getAnchorIndex() {
        if (representativeAnchor == null)
            return anchorIndex;
        
        return getToolBar().getRepresentativeAnchorIndex(representativeAnchor);
    }

    public boolean isAvailableCountable() {
        return true;
    }

    public void updateRepresentativeAnchor() {
        if (representativeAnchor != null) {
            ToolWindowAnchor anchor = getAnchor();

            String labelText = SwingUtil.getUserString(content.getRepresentativeAnchorDescriptor().getTitle());
            Icon toolIcon = content.getIcon();

            JLabel representativeLabel = (JLabel) representativeAnchor;
            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeLabel.setIcon(toolIcon);
                    representativeLabel.setText(labelText);
                    break;
                case LEFT:
                    if (SwingUtil.getBoolean(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false) && toolIcon != null)
                        toolIcon = GraphicsUtil.rotate(toolIcon, -Math.PI/2);

                    TextIcon textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeLabel.getIcon()).getLeftIcon()).getComponent(), labelText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);

                    representativeLabel.setText(null);
                    representativeLabel.setIcon(compositeIcon);
                    break;
                case RIGHT:
                    if (SwingUtil.getBoolean(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false) && toolIcon != null)
                        toolIcon = GraphicsUtil.rotate(toolIcon, Math.PI/2);

                    textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeLabel.getIcon()).getRightIcon()).getComponent(), labelText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);

                    representativeLabel.setText(null);
                    representativeLabel.setIcon(compositeIcon);
                    break;
            }
        }
    }

    public void cleanup() {
        getCleaner().cleanup();

        content.removePropertyChangeListener(this);

        content = null;
        manager = null;
    }

    public boolean isDragImageAvailable() {
        return false;
    }

    public Component getComponentForDragImage() {
        return null;
    }

}
