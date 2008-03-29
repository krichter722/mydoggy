package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.CustomDockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.AggregateIcon;
import org.noos.xing.mydoggy.plaf.ui.cmp.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.look.ContentRepresentativeAnchorUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentDescriptor extends CustomDockableDescriptor implements PropertyChangeListener {
    protected Content content;


    public ContentDescriptor(MyDoggyToolWindowManager manager, Content content) {
        super(manager, ToolWindowAnchor.LEFT);
        this.content = content;
        this.anchor = ToolWindowAnchor.LEFT;

        content.addPropertyChangeListener(this);   // how to remove this????
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

    public JComponent getRepresentativeAnchor(Component container) {
        if (representativeAnchor == null) {
            ToolWindowAnchor anchor = getAnchor();

            String labelText = getResourceManager().getUserString(content.getId());
            Icon toolIcon = content.getIcon();

            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeAnchor = new RepresentativeAnchorLabel(labelText, toolIcon, JLabel.CENTER);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(container, labelText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new RepresentativeAnchorLabel(compositeIcon, JLabel.CENTER);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(container, labelText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new RepresentativeAnchorLabel(compositeIcon, JLabel.CENTER);
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
            return anchorIndex;
        
        return getToolBar().getRepresentativeAnchorIndex(representativeAnchor);
    }

    public void updateRepresentativeAnchor() {
        if (representativeAnchor != null) {
            ToolWindowAnchor anchor = getAnchor();

            String labelText = getResourceManager().getUserString(content.getId());
            Icon toolIcon = content.getIcon();

            JLabel representativeLabel = (JLabel) representativeAnchor;
            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeLabel.setIcon(toolIcon);
                    representativeLabel.setText(labelText);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeLabel.getIcon()).getLeftIcon()).getComponent(), labelText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeLabel.setText(null);
                    representativeLabel.setIcon(compositeIcon);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeLabel.getIcon()).getRightIcon()).getComponent(), labelText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeLabel.setText(null);
                    representativeLabel.setIcon(compositeIcon);
                    break;
            }
        }
    }


    public boolean isDragImageAvailable() {
        return false;
    }

    public Component getComponentForDragImage() {
        return null;
    }


    public class RepresentativeAnchorLabel extends JLabel {

        public RepresentativeAnchorLabel(Icon image, int horizontalAlignment) {
            super(image, horizontalAlignment);
            super.setUI((LabelUI) createRepresentativeAnchorUI());
        }

        public RepresentativeAnchorLabel(String text, Icon icon, int horizontalAlignment) {
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
