package org.noos.xing.mydoggy.itest.impl.tracer;

import org.noos.xing.mydoggy.itest.Tracer;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolkitTracer implements Tracer {
    private boolean mounted;
    private boolean running;


    public ToolkitTracer() {
        this.mounted = false;
    }

    public void start() {
        mount();
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    protected void mount() {
        if (!mounted) {
            Toolkit.getDefaultToolkit().addAWTEventListener(new MouseEventListener(),
                                                            AWTEvent.MOUSE_EVENT_MASK);
            mounted = true;
        }
    }

    class MouseEventListener implements AWTEventListener {
        public void eventDispatched(AWTEvent event) {
            if (!running)
                return;

            MouseEvent me = (MouseEvent) event;
            String name = me.getComponent().getName();
            if (name == null)
                return;

            switch(me.getID()) {
                case MouseEvent.MOUSE_CLICKED :
                    System.out.println("click on " + name + " on " + me.getPoint());
                    break;
            }
        }
    }
}
