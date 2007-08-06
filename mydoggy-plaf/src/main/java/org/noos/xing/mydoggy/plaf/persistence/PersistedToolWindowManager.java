package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.PushAwayMode;
import org.xml.sax.Attributes;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedToolWindowManager {
    private PushAwayMode pushAwayMode;
    private int dividerLeft, dividerRight, dividerTop, dividerBottom;

    public PersistedToolWindowManager() {
    }

    public PushAwayMode getPushAwayMode() {
        return pushAwayMode;
    }

    public void setPushAwayMode(PushAwayMode pushAwayMode) {
        this.pushAwayMode = pushAwayMode;
    }

    public void update(Attributes attributes) {
        this.dividerLeft = Integer.parseInt(attributes.getValue("dividerLeft"));
        this.dividerRight = Integer.parseInt(attributes.getValue("dividerRight"));
        this.dividerTop = Integer.parseInt(attributes.getValue("dividerTop"));
        this.dividerBottom = Integer.parseInt(attributes.getValue("dividerBottom"));
    }

    public int getDividerLeft() {
        return dividerLeft;
    }

    public int getDividerRight() {
        return dividerRight;
    }

    public int getDividerTop() {
        return dividerTop;
    }

    public int getDividerBottom() {
        return dividerBottom;
    }
}
