package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;

/**
 * This interface is used to mark and define common behaviours for those classes that manage
 * how a toolwindow type is rendered.
 *
 * @author Angelo De Caro
 */
public interface ToolWindowContainer extends Cleaner {


    void updateUI();

}
