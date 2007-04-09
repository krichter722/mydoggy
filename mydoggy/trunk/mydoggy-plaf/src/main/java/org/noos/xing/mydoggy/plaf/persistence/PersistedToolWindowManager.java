package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.PushAwayMode;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PersistedToolWindowManager {
    private PushAwayMode pushAwayMode;

    public PersistedToolWindowManager() {
    }

    public PushAwayMode getPushAwayMode() {
        return pushAwayMode;
    }

    public void setPushAwayMode(PushAwayMode pushAwayMode) {
        this.pushAwayMode = pushAwayMode;
    }
}
