package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.content.ContentDescriptor;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentRepresentativeAnchor extends JLabel implements Cleaner {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ContentRepresentativeAnchorUI";


    protected ContentDescriptor contentDescriptor;


    public ContentRepresentativeAnchor(ContentDescriptor contentDescriptor,
                                       Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        this.contentDescriptor = contentDescriptor;

        contentDescriptor.getCleaner().addCleaner(this);

        updateUI();
    }

    public ContentRepresentativeAnchor(ContentDescriptor contentDescriptor,
                                       String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
        this.contentDescriptor = contentDescriptor;

        contentDescriptor.getCleaner().addCleaner(this);

        updateUI();
    }


    public void cleanup() {
        this.contentDescriptor = null;
    }

    public void updateUI() {
        if (contentDescriptor != null)
            super.updateUI();
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }


    public DockableDescriptor getDockableDescriptor() {
        return contentDescriptor;
    }

}