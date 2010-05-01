package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;

import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class DefaultExternTypeDescriptor implements ToolWindowTypeDescriptor {

    public ToolWindowType getType() {
        return ToolWindowType.EXTERN;
    }

    public void setAnimating(boolean animating) {

    }

    public boolean isAnimating() {
        return false;
    }

    public void setIdVisibleOnTitleBar(boolean idVisibleOnTitleBar) {

    }

    public boolean isIdVisibleOnTitleBar() {
        return false;
    }

    public void setTitleBarButtonsVisible(boolean titleBarButtonsVisible) {

    }

    public boolean isTitleBarButtonsVisible() {
        return false;
    }

    public void setTitleBarVisible(boolean titleBarVisible) {

    }

    public boolean isTitleBarVisible() {
        return false;
    }

    public void setAutoHide(boolean autoHide) {

    }

    public boolean isAutoHide() {
        return false;
    }

    public void setEnabled(boolean enabled) {

    }

    public boolean isEnabled() {
        return false;
    }

    public void setHideRepresentativeButtonOnVisible(boolean hideRepresentativeButtonOnVisible) {

    }

    public boolean isHideRepresentativeButtonOnVisible() {
        return false;
    }

    public void addToolWindowAction(ToolWindowAction toolWindowAction) {

    }

    public void addToolWindowAction(ToolWindowAction toolWindowAction, int index) {

    }

    public ToolWindowAction getToolWindowAction(String id) {
        return null;
    }

    public ToolWindowAction[] getToolWindowActions() {
        return new ToolWindowAction[0];
    }

    public void removeToolWindowAction(String id) {

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {

    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {

    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return new PropertyChangeListener[0];
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {

    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {

    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return new PropertyChangeListener[0];
    }
}
