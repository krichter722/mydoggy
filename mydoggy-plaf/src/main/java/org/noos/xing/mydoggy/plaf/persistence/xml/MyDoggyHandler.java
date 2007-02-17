package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.persistence.PersistedDockedType;
import org.noos.xing.mydoggy.plaf.persistence.PersistedFloatingType;
import org.noos.xing.mydoggy.plaf.persistence.PersistedToolWindow;
import org.noos.xing.mydoggy.plaf.persistence.PersistedSlidingType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyHandler extends DefaultHandler {
    enum State {
        NOP, MYDOGGY, TOOLS, TOOL, DESCRIPTOR
    }

    private ToolWindowManager toolWindowManager;

    private State state;
    private State subState;
    private PersistedToolWindow persistedToolWindow;
    private PersistedDockedType dockedType;
    private PersistedSlidingType slidingType;
    private PersistedFloatingType floatingType;


    public MyDoggyHandler(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public void startDocument() throws SAXException {
        state = State.MYDOGGY;
        subState = State.NOP;
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // TODO: check for errors...
        switch (state) {
            case MYDOGGY:
                if ("mydoggy".equals(qName)) {
                    // Check version
                    if (attributes.getLength() <= 0)
                        throw new SAXException("Invalid version. Not defined.");

                    String version = attributes.getValue(0);
                    if (!"1.2.0".equals(version))
                        throw new SAXException("Invalid version. " + version);

                    if (attributes.getValue("pushAwayMode") != null)
                        toolWindowManager.getToolWindowManagerDescriptor().setPushAwayMode(
                                ToolWindowManagerDescriptor.PushAwayMode.valueOf(attributes.getValue("pushAwayMode"))
                        );

                    state = State.TOOLS;
                } else
                    throw new SAXException("Invalid element at this position. Expecting <mydoggy>");
                break;
            case TOOLS:
                if (!"tools".equals(qName))
                    throw new SAXException("Invalid element at this position. Expecting <tools>");
                state = State.TOOL;
                break;
            case TOOL:
                if (subState == State.NOP) {
                    if (!"tool".equals(qName))
                        throw new SAXException("Invalid element at this position. Expecting <tool>");

                    persistedToolWindow = new PersistedToolWindow(attributes);
                    dockedType = null;
                    slidingType = null;
                    floatingType = null;
                    subState = State.DESCRIPTOR;
                } else {
                    if ("docked".equals(qName)) {
                        dockedType = new PersistedDockedType(attributes);
                    } else if ("sliding".equals(qName)) {
                        slidingType = new PersistedSlidingType(attributes);
                    } else if ("floating".equals(qName)) {
                        floatingType = new PersistedFloatingType(attributes);
                    }
                }
                break;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (state) {
            case TOOL:
                if ("tool".equals(qName)) {
                    subState = State.NOP;

                    ToolWindow toolWindow = toolWindowManager.getToolWindow(persistedToolWindow.getId());

                    // Apply descriptors
                    if (dockedType != null) {
                        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
                        descriptor.setDockLength(dockedType.getDockLength());
                        descriptor.setPopupMenuEnabled(dockedType.isPopupMenuEnabled());
                    }

                    if (slidingType != null) {
                        SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
                        descriptor.setEnabled(slidingType.isEnabled());
                        descriptor.setTransparentDelay(slidingType.getTransparentDelay());
                        descriptor.setTransparentMode(slidingType.isTransparentMode());
                        descriptor.setTransparentRatio(slidingType.getTransparentRatio());
                    }

                    if (floatingType != null) {
                        FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
                        descriptor.setEnabled(floatingType.isEnabled());
                        descriptor.setTransparentDelay(floatingType.getTransparentDelay());
                        descriptor.setTransparentMode(floatingType.isTransparentMode());
                        descriptor.setTransparentRatio(floatingType.getTransparentRatio());
                        descriptor.setModal(floatingType.isModal());

                        Point point = floatingType.getLocation();
                        if (point != null)
                            descriptor.setLocation(point.x, point.y);
                        Dimension dimension = floatingType.getSize();
                        if (dimension != null)
                            descriptor.setSize(dimension.width, dimension.height);
                    }

                    toolWindow.setAnchor(persistedToolWindow.getAnchor());
                    toolWindow.setType(persistedToolWindow.getType());
                    toolWindow.setAutoHide(persistedToolWindow.isAutoHide());
                    toolWindow.setAvailable(persistedToolWindow.isAvailable());

                    if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                        toolWindow.setVisible(persistedToolWindow.isVisible());
                    } else {
                        if (persistedToolWindow.isVisible())
                            toolWindow.aggregate();
                        else
                            toolWindow.setVisible(false);
                    }
                    toolWindow.setActive(persistedToolWindow.isActive());
                }
                break;
            case TOOLS:
                state = State.TOOLS;
                break;
            case MYDOGGY:
                state = State.MYDOGGY;
                break;
        }
    }

}
