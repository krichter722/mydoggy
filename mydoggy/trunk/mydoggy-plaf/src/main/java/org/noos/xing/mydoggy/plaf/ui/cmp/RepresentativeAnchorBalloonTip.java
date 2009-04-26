package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.RepresentativeAnchorDescriptor;
import org.noos.xing.mydoggy.plaf.ui.look.RepresentativeAnchorBalloonTipUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RepresentativeAnchorBalloonTip extends JPanel {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "RepresentativeAnchorBalloonTipUI";



    protected RepresentativeAnchorDescriptor representativeAnchorDescriptor;
    protected Component representativeAnchor;

    protected String text;
    protected Icon icon;


    public RepresentativeAnchorBalloonTip(RepresentativeAnchorDescriptor representativeAnchorDescriptor, Component representativeAnchor) {
        this.representativeAnchorDescriptor = representativeAnchorDescriptor;
        this.representativeAnchor = representativeAnchor;
        
        updateUI();
    }


    public void updateUI() {
        if (representativeAnchorDescriptor != null)
            setUI((RepresentativeAnchorBalloonTipUI) UIManager.getUI(this));
    }

    public void setUI(RepresentativeAnchorBalloonTipUI ui) {
        super.setUI(ui);
    }

    public RepresentativeAnchorBalloonTipUI getUI() {
        return (RepresentativeAnchorBalloonTipUI) super.getUI();
    }

    public String getUIClassID() {
        return uiClassID;
    }


    public void showTip() {
        getUI().showTip();
    }

    public void hideTip() {
        getUI().hideTip();
    }

    public void setRootPaneContainer(RootPaneContainer rootPaneContainer) {
        firePropertyChange("rootPaneContainer", this, rootPaneContainer);
    }

    public void setText(String text) {
        String old = this.text;
        this.text = text;

        firePropertyChange("text", old, text);
    }

    public String getText() {
        return text;
    }

    public void setIcon(Icon icon) {
        Icon old = this.icon;
        this.icon = icon;
        
        firePropertyChange("icon", old, icon);
    }

    public Icon getIcon() {
        return icon;
    }

    public RepresentativeAnchorDescriptor getRepresentativeAnchorDescriptor() {
        return representativeAnchorDescriptor;
    }

    public Component getRepresentativeAnchor() {
        return representativeAnchor;
    }

}
