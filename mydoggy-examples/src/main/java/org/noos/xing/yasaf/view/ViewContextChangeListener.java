package org.noos.xing.yasaf.view;

import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

public interface ViewContextChangeListener extends java.util.EventListener {

    void contextChange(ViewContextChangeEvent evt);

}