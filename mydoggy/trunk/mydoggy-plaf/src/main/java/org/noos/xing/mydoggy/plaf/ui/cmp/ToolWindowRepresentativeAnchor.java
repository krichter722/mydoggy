package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class ToolWindowRepresentativeAnchor extends JLabel implements Cleaner {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ToolWindowRepresentativeAnchorUI";


    protected ToolWindowDescriptor toolWindowDescriptor;


    public ToolWindowRepresentativeAnchor(ToolWindowDescriptor toolWindowDescriptor,
                                          Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        this.toolWindowDescriptor = toolWindowDescriptor;

        toolWindowDescriptor.getCleaner().addCleaner(this);

        updateUI();
    }

    public ToolWindowRepresentativeAnchor(ToolWindowDescriptor toolWindowDescriptor,
                                          String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        this.toolWindowDescriptor = toolWindowDescriptor;

        toolWindowDescriptor.getCleaner().addCleaner(this);

        updateUI();
    }


    public void cleanup() {
        this.toolWindowDescriptor = null;
    }

    public void updateUI() {
        if (toolWindowDescriptor != null)
            super.updateUI();
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }


    public ToolWindowDescriptor getDockableDescriptor() {
        return toolWindowDescriptor;
    }

}
