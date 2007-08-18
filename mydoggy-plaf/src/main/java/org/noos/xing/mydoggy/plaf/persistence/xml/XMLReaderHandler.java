package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.persistence.*;
import org.noos.xing.mydoggy.plaf.persistence.merge.MergePolicyApplier;
import org.noos.xing.mydoggy.plaf.persistence.merge.ResetMergePolicy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XMLReaderHandler extends DefaultHandler {
    enum State {
        NOP,
        MYDOGGY,
        SUB_SECTION,
        TOOL,
        DESCRIPTORS,
        TOOL_WINDOW_MANAGER_DESCRIPTOR,
        PUSH_AWAY_MODE,
        PUSH_AWAY_MOST_RECENT_MODE,
        CONTENT_MANAGER
    }

    private ToolWindowManager toolWindowManager;
    private MergePolicyApplier mergePolicyApplier;

    private State state;
    private State subState;

    private PersistedToolWindowManager persistedToolWindowManager;
    private PersistedToolWindow persistedToolWindow;
    private PersistedDockedType dockedType;
    private PersistedSlidingType slidingType;
    private PersistedFloatingType floatingType;
    private PersistedMostRecentDescriptor persistedMostRecentDescriptor;
    private PersistedContentManager persistedContentManager;

    private Map<ToolWindow, PersistedToolWindow> map;


    public XMLReaderHandler(ToolWindowManager toolWindowManager, MergePolicyApplier mergePolicyApplier) {
        this.toolWindowManager = toolWindowManager;
        this.mergePolicyApplier = mergePolicyApplier;
        if (mergePolicyApplier == null)
            this.mergePolicyApplier = new ResetMergePolicy();
    }

    public void startDocument() throws SAXException {
        state = State.MYDOGGY;
        subState = State.NOP;
        this.map = new Hashtable<ToolWindow, PersistedToolWindow>();
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (state) {
            case MYDOGGY:
                if ("mydoggy".equals(qName)) {
                    // Check version
                    if (attributes.getLength() <= 0)
                        throw new SAXException("Invalid version. Not defined version attribute.");

                    String version = attributes.getValue("version");
                    if (!"1.3.1".equals(version))
                        throw new SAXException("Invalid version : " + version);

                    persistedToolWindowManager = new PersistedToolWindowManager();
                    if (attributes.getValue("pushAwayMode") != null)
                        persistedToolWindowManager.setPushAwayMode(PushAwayMode.valueOf(attributes.getValue("pushAwayMode")));

                    state = State.SUB_SECTION;
                } else
                    throw new SAXException("Invalid element at this position. Expecting <mydoggy>");
                break;
            case SUB_SECTION :
                if ("toolWindowDescriptorManager".equals(qName)) {
                    state = State.TOOL_WINDOW_MANAGER_DESCRIPTOR;
                    persistedToolWindowManager.update(attributes);                    
                } else if ("tools".equals(qName)) {
                    state = State.TOOL;
                } else if ("contentManager".equals(qName)) {
                    state = State.CONTENT_MANAGER;
                    persistedContentManager = new PersistedContentManager();
                } else
                    throw new SAXException("Invalid element at this position. Expecting <pushAway> or <tools> or <contentManager>");
                break;
            case TOOL_WINDOW_MANAGER_DESCRIPTOR:
                if ("pushAway".equals(qName))
                    state = State.PUSH_AWAY_MODE;
                break;
            case PUSH_AWAY_MODE:
                if ("mode".equals(qName)) {
                    // Load policy
                    if (!PushAwayMode.MOST_RECENT.toString().equals(attributes.getValue("type")))
                        throw new SAXException("Invalid PushAwayMode policy. Supported only PushAwayMode.MOST_RECENT");

                    persistedMostRecentDescriptor = new PersistedMostRecentDescriptor();
                    subState = State.PUSH_AWAY_MOST_RECENT_MODE;
                } else {
                    switch (subState) {
                        case PUSH_AWAY_MOST_RECENT_MODE:
                            if (!"anchor".equals(qName))
                                throw new SAXException("Invalid element at this position. Expecting <anchor>");
                            persistedMostRecentDescriptor.push(ToolWindowAnchor.valueOf(attributes.getValue("type")));
                            break;
                    }
                }
                break;
            case TOOL:
                if (subState == State.NOP) {
                    if (!"tool".equals(qName))
                        throw new SAXException("Invalid element at this position. Expecting <tool>");

                    persistedToolWindow = new PersistedToolWindow(attributes);
                    dockedType = null;
                    slidingType = null;
                    floatingType = null;
                    subState = State.DESCRIPTORS;
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
            case CONTENT_MANAGER:
                if ("content".equals(qName)) {
                    persistedContentManager.getContents().add(new PersistedContent(attributes));
                } else
                    throw new SAXException("Invalid element at this position. Expecting <content>");

                break;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (state) {
            case TOOL:
                if ("tool".equals(qName)) {
                    subState = State.NOP;

                    ToolWindow toolWindow = toolWindowManager.getToolWindow(persistedToolWindow.getId());
                    if (toolWindow != null) {
                        // Apply descriptors
                        if (dockedType != null) {
                            DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
                            descriptor.setDockLength(dockedType.getDockLength());
                            descriptor.setPopupMenuEnabled(dockedType.isPopupMenuEnabled());
                            descriptor.setAnimating(dockedType.isAnimating());
                            descriptor.setPreviewEnabled(dockedType.isPreviewEnabled());
                            descriptor.setPreviewDelay(dockedType.getPreviewDelay());
                            descriptor.setPreviewTransparentRatio(dockedType.getPreviewTransparentRatio());
                            descriptor.setIdVisibleOnTitleBar(dockedType.isIdVisibleOnTitleBar());
                            descriptor.setHideRepresentativeButtonOnVisible(dockedType.isHideRepresentativeButtonOnVisible());
                        }

                        if (slidingType != null) {
                            SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
                            descriptor.setEnabled(slidingType.isEnabled());
                            descriptor.setTransparentDelay(slidingType.getTransparentDelay());
                            descriptor.setTransparentMode(slidingType.isTransparentMode());
                            descriptor.setTransparentRatio(slidingType.getTransparentRatio());
                            descriptor.setAnimating(slidingType.isAnimating());
                        }

                        if (floatingType != null) {
                            FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
                            descriptor.setEnabled(floatingType.isEnabled());
                            descriptor.setTransparentDelay(floatingType.getTransparentDelay());
                            descriptor.setTransparentMode(floatingType.isTransparentMode());
                            descriptor.setTransparentRatio(floatingType.getTransparentRatio());
                            descriptor.setModal(floatingType.isModal());
                            descriptor.setAnimating(floatingType.isAnimating());

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
                        toolWindow.setIndex(persistedToolWindow.getIndex());
                        toolWindow.setAggregateMode(persistedToolWindow.isAggregateMode());

                        map.put(toolWindow, persistedToolWindow);
                    }
                } else if ("tools".equals(qName)) {
                    state = State.SUB_SECTION;
                    subState = State.NOP;
                }
                break;
            case MYDOGGY:
                state = State.MYDOGGY;
                break;
            case TOOL_WINDOW_MANAGER_DESCRIPTOR :
                if ("toolWindowDescriptorManager".equals(qName)) {
                    state = State.SUB_SECTION;
                    subState = State.NOP;
                }
                break;                
            case PUSH_AWAY_MODE:
                if ("pushAway".equals(qName)) {
                    state = State.TOOL_WINDOW_MANAGER_DESCRIPTOR;
                    subState = State.NOP;
                }
                break;
        }
        if ("mydoggy".equals(qName)) {
            applyTo(ToolWindowAnchor.LEFT);
            applyTo(ToolWindowAnchor.BOTTOM);
            applyTo(ToolWindowAnchor.RIGHT);
            applyTo(ToolWindowAnchor.TOP);

            // PushAwayModeDescriptors
            if (persistedMostRecentDescriptor != null)
                ((MostRecentDescriptor) toolWindowManager.getToolWindowManagerDescriptor().getPushAwayModeDescriptor(PushAwayMode.MOST_RECENT)).
                        append(persistedMostRecentDescriptor.getStack().toArray(new ToolWindowAnchor[persistedMostRecentDescriptor.getStack().size()]));

            if (persistedToolWindowManager.getPushAwayMode() != null) {
                ToolWindowManagerDescriptor managerDescriptor = toolWindowManager.getToolWindowManagerDescriptor();
                managerDescriptor.setPushAwayMode(persistedToolWindowManager.getPushAwayMode());

                managerDescriptor.setDividerSize(ToolWindowAnchor.LEFT, persistedToolWindowManager.getDividerLeft());
                managerDescriptor.setDividerSize(ToolWindowAnchor.RIGHT, persistedToolWindowManager.getDividerRight());
                managerDescriptor.setDividerSize(ToolWindowAnchor.TOP, persistedToolWindowManager.getDividerTop());
                managerDescriptor.setDividerSize(ToolWindowAnchor.BOTTOM, persistedToolWindowManager.getDividerBottom());
                managerDescriptor.setNumberingEnabled(persistedToolWindowManager.isNumberingEnabled());
            }

            // ContentManager
            Content selectedContent = null;
            for (PersistedContent persistedContent : persistedContentManager.getContents()) {
                Content content = toolWindowManager.getContentManager().getContent(persistedContent.getKey());

                if (content != null) {
                    if (persistedContent.isSelected())
                        selectedContent = content;

                    content.setEnabled(persistedContent.isEnabled());
                    content.setDetached(persistedContent.isDetached());
                }
            }

            if (selectedContent != null)
                selectedContent.setSelected(true);
        }
    }

    protected void applyTo(ToolWindowAnchor anchor) {
        ToolWindow activeTool = null;
        ToolWindow maximizedTool = null;

        for (ToolWindow toolWindow : map.keySet()) {
            if (toolWindow.getAnchor() != anchor)
                continue;
            persistedToolWindow = map.get(toolWindow);

            mergePolicyApplier.applyToolWindow(toolWindow, persistedToolWindow);

            if (persistedToolWindow.isActive())
                activeTool = toolWindow;

            if (persistedToolWindow.isMaximized())
                maximizedTool = toolWindow;
        }
        
        if (activeTool != null)
            activeTool.setActive(true);
        
        if (maximizedTool != null)
            maximizedTool.setMaximized(true);
    }

}
