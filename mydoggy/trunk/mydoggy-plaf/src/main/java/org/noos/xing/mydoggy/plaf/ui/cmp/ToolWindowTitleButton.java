package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTitleButtonUI;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTitleButton extends JButton {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowTitleButtonUI";


    public ToolWindowTitleButton() {
    }

    public ToolWindowTitleButton(Icon icon) {
        super(icon);
    }

    public ToolWindowTitleButton(String text) {
        super(text);
    }

    public ToolWindowTitleButton(Action a) {
        super(a);
    }

    public ToolWindowTitleButton(String text, Icon icon) {
        super(text, icon);
    }

    public void updateUI() {
        setUI((ToolWindowTitleButtonUI) UIManager.getUI(this));
    }

    public void setUI(ToolWindowTitleButtonUI ui) {
        super.setUI(ui);
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

}
