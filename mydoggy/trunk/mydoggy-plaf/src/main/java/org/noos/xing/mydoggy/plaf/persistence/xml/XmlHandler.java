package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.persistence.PersistedToolWindow;
import org.noos.xing.mydoggy.plaf.persistence.PersistedDockedType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XmlHandler extends DefaultHandler {
    enum State {NOP, MYDOGGY, TOOLS, TOOL, DESCRIPTOR}

    private ToolWindowManager toolWindowManager;

    private State state;
    private State subState;
    private PersistedToolWindow persistedToolWindow;
    private PersistedDockedType dockedType;


    public XmlHandler(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public void startDocument() throws SAXException {
        state = State.MYDOGGY;
        subState = State.NOP;
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch(state) {
            case MYDOGGY:
                if ("mydoggy".equals(qName)) {
                    // Check version
                    if (attributes.getLength() <= 0)
                        throw new SAXException("Invalid version. Not defined.");

                    String version = attributes.getValue(0);
                    if (!"1.0".equals(version))
                        throw new SAXException("Invalid version. " + version);
                    state = State.TOOLS;
                } else
                    throw new SAXException("Invalid element at this position. Aspeting <mydoggy>");
                break;
            case TOOLS:
                if (!"tools".equals(qName))
                    throw new SAXException("Invalid element at this position. Aspeting <tools>");
                state = State.TOOL;
                break;
            case TOOL:
                if (subState == State.NOP) {
                    if (!"tool".equals(qName))
                        throw new SAXException("Invalid element at this position. Aspeting <tool>");

                    persistedToolWindow = new PersistedToolWindow(attributes);
                    dockedType = null;
                    subState = State.DESCRIPTOR;
                } else {
                    if ("docked".equals(qName)) {
                        dockedType = new PersistedDockedType(attributes);
                    }
                }
                break;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch(state) {
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
                    // TODO: to be continued...

                    toolWindow.setAnchor(persistedToolWindow.getAnchor());
                    toolWindow.setType(persistedToolWindow.getType());
                    toolWindow.setAutoHide(persistedToolWindow.isAutoHide());
                    toolWindow.setAvailable(persistedToolWindow.isAvailable());
                    if (persistedToolWindow.isVisible())
                        toolWindow.aggregate();
                    else
                        toolWindow.setVisible(false);
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
