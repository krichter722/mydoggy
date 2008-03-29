package org.noos.xing.yasaf.view;

import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

// TODO: add validation condition to accept event...
public interface ViewContextChangeListener extends java.util.EventListener {

    void contextChange(ViewContextChangeEvent evt);

}