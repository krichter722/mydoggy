package org.noos.xing.mydoggy.plaf.ui;

import junit.framework.TestCase;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitContainer;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TestMultiSplitContainer extends TestCase {
    private MultiSplitContainer multiSplitContainer;

    protected void setUp() throws Exception {
        this.multiSplitContainer = new MultiSplitContainer(JSplitPane.VERTICAL_SPLIT);
    }

    public void testInstance() {
        assertNotNull(multiSplitContainer);
        assertTrue(multiSplitContainer.isEmpty());
    }
    
    public void testAddContent() {
        multiSplitContainer.addContent(new JLabel());
        assertFalse(multiSplitContainer.isEmpty());
    }
}
