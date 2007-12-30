package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.persistence.xml.merge.MergePolicyApplier;
import org.noos.xing.mydoggy.plaf.persistence.xml.merge.ResetMergePolicy;
import org.noos.xing.mydoggy.plaf.persistence.xml.merge.UnionMergePolicy;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitDockableContainer;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XMLPersistenceDelegate implements PersistenceDelegate {
    protected MyDoggyToolWindowManager toolWindowManager;

    protected Map<MergePolicy, MergePolicyApplier> mergePolicyApplierMap;
    protected Map<String, ElementParser> elementParserMap;

    public XMLPersistenceDelegate(MyDoggyToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        initMaps();
    }

    public void save(OutputStream outputStream) {
        try {
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(outputStream));

            writer.startDocument();

            AttributesImpl mydoggyAttributes = new AttributesImpl();
            mydoggyAttributes.addAttribute(null, "version", null, null, "1.4.0");
            writer.startElement("mydoggy", mydoggyAttributes);

            saveToolWindows(writer);
            saveToolWindowManagerDescriptor(writer);
            saveContentManager(writer);
            saveBars(writer);

            writer.endElement("mydoggy");
            writer.endDocument();

            writer.flush();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void apply(InputStream inputStream) {
        merge(inputStream, MergePolicy.RESET);
    }

    public void merge(InputStream inputStream, MergePolicy mergePolicy) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            parseElement(document.getDocumentElement(), mergePolicyApplierMap.get(mergePolicy));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    protected void initMaps() {
        mergePolicyApplierMap = new Hashtable<MergePolicy, MergePolicyApplier>();
        mergePolicyApplierMap.put(PersistenceDelegate.MergePolicy.RESET, new ResetMergePolicy());
        mergePolicyApplierMap.put(PersistenceDelegate.MergePolicy.UNION, new UnionMergePolicy());

        elementParserMap = new Hashtable<String, ElementParser>();
        elementParserMap.put("mydoggy", new MyDoggyElementParser());
        elementParserMap.put("toolWindowManagerDescriptor", new ToolWindowManagerDescriptorElementParser());
        elementParserMap.put("aggregateMode", new AggregateModeElementParser());
        elementParserMap.put("dividerSize", new DividerSizeElementParser());
        elementParserMap.put("pushAway", new PushAwayModeElementParser());
        elementParserMap.put("tools", new ToolsElementParser());
        elementParserMap.put("contentManager", new ContentManagerElementParser());
        elementParserMap.put("bar", new BarElementParser());
    }


    protected void saveToolWindows(XMLWriter writer) throws SAXException {
        writer.startElement("tools");

        for (ToolWindow toolWindow : toolWindowManager.getToolWindows())
            saveToolWindow(writer, toolWindow);

        writer.endElement("tools");
    }

    protected void saveToolWindow(XMLWriter writer, ToolWindow toolWindow) throws SAXException {
        AttributesImpl toolAttributes = new AttributesImpl();
        toolAttributes.addAttribute(null, "id", null, null, String.valueOf(toolWindow.getId()));
        toolAttributes.addAttribute(null, "available", null, null, String.valueOf(toolWindow.isAvailable()));
        toolAttributes.addAttribute(null, "visible", null, null, String.valueOf(toolWindow.isVisible()));
        toolAttributes.addAttribute(null, "active", null, null, String.valueOf(toolWindow.isActive()));
        toolAttributes.addAttribute(null, "autoHide", null, null, String.valueOf(toolWindow.isAutoHide()));
        toolAttributes.addAttribute(null, "anchor", null, null, String.valueOf(toolWindow.getAnchor()));
        toolAttributes.addAttribute(null, "anchorIndex", null, null, String.valueOf(toolWindow.getAnchorIndex()));
        toolAttributes.addAttribute(null, "type", null, null, String.valueOf(toolWindow.getType()));
        toolAttributes.addAttribute(null, "aggregateMode", null, null, String.valueOf(toolWindow.isAggregateMode()));
        toolAttributes.addAttribute(null, "maximized", null, null, String.valueOf(toolWindow.isMaximized()));
        toolAttributes.addAttribute(null, "index", null, null, String.valueOf(toolWindow.getIndex()));
        toolAttributes.addAttribute(null, "representativeAnchorButtonVisible", null, null, String.valueOf(toolWindow.isRepresentativeAnchorButtonVisible()));
        writer.startElement("tool", toolAttributes);

        writer.startElement("descriptors");

        // DockedTypeDescriptor
        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        AttributesImpl dockedDescriptorAttributes = new AttributesImpl();
        dockedDescriptorAttributes.addAttribute(null, "dockLength", null, null, String.valueOf(dockedTypeDescriptor.getDockLength()));
        dockedDescriptorAttributes.addAttribute(null, "popupMenuEnabled", null, null, String.valueOf(dockedTypeDescriptor.isPopupMenuEnabled()));
        dockedDescriptorAttributes.addAttribute(null, "animating", null, null, String.valueOf(dockedTypeDescriptor.isAnimating()));
        dockedDescriptorAttributes.addAttribute(null, "previewEnabled", null, null, String.valueOf(dockedTypeDescriptor.isPreviewEnabled()));
        dockedDescriptorAttributes.addAttribute(null, "previewDelay", null, null, String.valueOf(dockedTypeDescriptor.getPreviewDelay()));
        dockedDescriptorAttributes.addAttribute(null, "previewTransparentRatio", null, null, String.valueOf(dockedTypeDescriptor.getPreviewTransparentRatio()));
        dockedDescriptorAttributes.addAttribute(null, "hideRepresentativeButtonOnVisible", null, null, String.valueOf(dockedTypeDescriptor.isHideRepresentativeButtonOnVisible()));
        dockedDescriptorAttributes.addAttribute(null, "idVisibleOnTitleBar", null, null, String.valueOf(dockedTypeDescriptor.isIdVisibleOnTitleBar()));
        writer.dataElement("docked", dockedDescriptorAttributes);

        // DockedTypeDescriptor
        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
        AttributesImpl slidingDescriptorAttributes = new AttributesImpl();
        slidingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(slidingTypeDescriptor.isTransparentMode()));
        slidingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(slidingTypeDescriptor.getTransparentDelay()));
        slidingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(slidingTypeDescriptor.getTransparentRatio()));
        slidingDescriptorAttributes.addAttribute(null, "enabled", null, null, String.valueOf(slidingTypeDescriptor.isEnabled()));
        slidingDescriptorAttributes.addAttribute(null, "animating", null, null, String.valueOf(slidingTypeDescriptor.isAnimating()));
        slidingDescriptorAttributes.addAttribute(null, "idVisibleOnTitleBar", null, null, String.valueOf(slidingTypeDescriptor.isIdVisibleOnTitleBar()));
        writer.dataElement("sliding", slidingDescriptorAttributes);

        // FloatingTypeDescriptor
        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
        AttributesImpl floatingDescriptorAttributes = new AttributesImpl();
        floatingDescriptorAttributes.addAttribute(null, "modal", null, null, String.valueOf(floatingTypeDescriptor.isModal()));
        floatingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(floatingTypeDescriptor.isTransparentMode()));
        floatingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(floatingTypeDescriptor.getTransparentDelay()));
        floatingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(floatingTypeDescriptor.getTransparentRatio()));
        floatingDescriptorAttributes.addAttribute(null, "enabled", null, null, String.valueOf(floatingTypeDescriptor.isEnabled()));
        floatingDescriptorAttributes.addAttribute(null, "animating", null, null, String.valueOf(floatingTypeDescriptor.isAnimating()));
        floatingDescriptorAttributes.addAttribute(null, "idVisibleOnTitleBar", null, null, String.valueOf(floatingTypeDescriptor.isIdVisibleOnTitleBar()));

        Point point = floatingTypeDescriptor.getLocation();
        if (point != null) {
            floatingDescriptorAttributes.addAttribute(null, "x", null, null, String.valueOf(point.x));
            floatingDescriptorAttributes.addAttribute(null, "y", null, null, String.valueOf(point.y));
        }

        Dimension dimension = floatingTypeDescriptor.getSize();
        if (dimension != null) {
            floatingDescriptorAttributes.addAttribute(null, "width", null, null, String.valueOf(dimension.width));
            floatingDescriptorAttributes.addAttribute(null, "height", null, null, String.valueOf(dimension.height));
        }

        writer.dataElement("floating", floatingDescriptorAttributes);

        // FloatingLiveTypeDescriptor
        FloatingLiveTypeDescriptor floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
        AttributesImpl floatingLiveDescriptorAttributes = new AttributesImpl();
        floatingLiveDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(floatingLiveTypeDescriptor.isTransparentMode()));
        floatingLiveDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(floatingLiveTypeDescriptor.getTransparentDelay()));
        floatingLiveDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(floatingLiveTypeDescriptor.getTransparentRatio()));
        floatingLiveDescriptorAttributes.addAttribute(null, "enabled", null, null, String.valueOf(floatingLiveTypeDescriptor.isEnabled()));
        floatingLiveDescriptorAttributes.addAttribute(null, "animating", null, null, String.valueOf(floatingLiveTypeDescriptor.isAnimating()));
        floatingLiveDescriptorAttributes.addAttribute(null, "idVisibleOnTitleBar", null, null, String.valueOf(floatingLiveTypeDescriptor.isIdVisibleOnTitleBar()));

        point = floatingLiveTypeDescriptor.getLocation();
        if (point != null) {
            floatingLiveDescriptorAttributes.addAttribute(null, "x", null, null, String.valueOf(point.x));
            floatingLiveDescriptorAttributes.addAttribute(null, "y", null, null, String.valueOf(point.y));
        }

        dimension = floatingLiveTypeDescriptor.getSize();
        if (dimension != null) {
            floatingLiveDescriptorAttributes.addAttribute(null, "width", null, null, String.valueOf(dimension.width));
            floatingLiveDescriptorAttributes.addAttribute(null, "height", null, null, String.valueOf(dimension.height));
        }

        writer.dataElement("floatingLive", floatingLiveDescriptorAttributes);

        // End descriptors
        writer.endElement("descriptors");

        boolean addTabsTag = false;
        for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
            if (tab.getDockableDelegator() != null) {
                Dockable dockable = tab.getDockableDelegator();

                if (!addTabsTag) {
                    addTabsTag = true;
                    writer.startElement("tabs");
                }

                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute(null, "dockableId", null, null, dockable.getId());
                attributes.addAttribute(null, "selected", null, null, "" + tab.isSelected());
                writer.dataElement("tab", attributes);
            }
        }

        if (addTabsTag)
            writer.endElement("tabs");

        writer.endElement("tool");
    }

    protected void saveToolWindowManagerDescriptor(XMLWriter writer) throws SAXException {
        ToolWindowManagerDescriptor descriptor = toolWindowManager.getToolWindowManagerDescriptor();

        // Start toolWindowDescriptorManager
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(null, "numberingEnabled", null, null, String.valueOf(descriptor.isNumberingEnabled()));
        attributes.addAttribute(null, "previewEnabled", null, null, String.valueOf(descriptor.isPreviewEnabled()));
        writer.startElement("toolWindowManagerDescriptor", attributes);

        // dividerSize element
        attributes = new AttributesImpl();
        attributes.addAttribute(null, "left", null, null, String.valueOf(descriptor.getDividerSize(ToolWindowAnchor.LEFT)));
        attributes.addAttribute(null, "right", null, null, String.valueOf(descriptor.getDividerSize(ToolWindowAnchor.RIGHT)));
        attributes.addAttribute(null, "top", null, null, String.valueOf(descriptor.getDividerSize(ToolWindowAnchor.TOP)));
        attributes.addAttribute(null, "bottom", null, null, String.valueOf(descriptor.getDividerSize(ToolWindowAnchor.BOTTOM)));
        writer.dataElement("dividerSize", attributes);

        // aggregateMode element
        attributes = new AttributesImpl();
        attributes.addAttribute(null, "left", null, null, String.valueOf(descriptor.isAggregateMode(ToolWindowAnchor.LEFT)));
        attributes.addAttribute(null, "right", null, null, String.valueOf(descriptor.isAggregateMode(ToolWindowAnchor.RIGHT)));
        attributes.addAttribute(null, "top", null, null, String.valueOf(descriptor.isAggregateMode(ToolWindowAnchor.TOP)));
        attributes.addAttribute(null, "bottom", null, null, String.valueOf(descriptor.isAggregateMode(ToolWindowAnchor.BOTTOM)));
        writer.dataElement("aggregateMode", attributes);

        // Start pushAway
        attributes = new AttributesImpl();
        attributes.addAttribute(null, "pushAwayMode", null, null,
                                toolWindowManager.getToolWindowManagerDescriptor().getPushAwayMode().toString());
        writer.startElement("pushAway", attributes);

        // start MOST_RECENT policy
        attributes = new AttributesImpl();
        attributes.addAttribute(null, "type", null, null, String.valueOf(PushAwayMode.MOST_RECENT));
        writer.startElement("mode", attributes);

        MostRecentDescriptor mostRecentDescriptor = (MostRecentDescriptor) toolWindowManager.getToolWindowManagerDescriptor().getPushAwayModeDescriptor(PushAwayMode.MOST_RECENT);

        for (ToolWindowAnchor toolWindowAnchor : mostRecentDescriptor.getMostRecentAnchors()) {
            AttributesImpl anchorAttributes = new AttributesImpl();
            anchorAttributes.addAttribute(null, "type", null, null, String.valueOf(toolWindowAnchor));
            writer.dataElement("anchor", anchorAttributes);
        }

        // end MOST_RECENT policy
        writer.endElement("mode");

        // End pushAway
        writer.endElement("pushAway");

        // End toolWindowDescriptorManager
        writer.endElement("toolWindowManagerDescriptor");
    }

    protected void saveContentManager(XMLWriter writer) throws SAXException {
        // Start contentManager
        writer.startElement("contentManager");

        for (Content content : toolWindowManager.getContentManager().getContents()) {

            AttributesImpl contentAttributes = new AttributesImpl();
            contentAttributes.addAttribute(null, "id", null, null, content.getId());
            contentAttributes.addAttribute(null, "detached", null, null, String.valueOf(content.isDetached()));
            contentAttributes.addAttribute(null, "enabled", null, null, String.valueOf(content.isEnabled()));
            contentAttributes.addAttribute(null, "selected", null, null, String.valueOf(content.isSelected()));
            contentAttributes.addAttribute(null, "maximized", null, null, String.valueOf(content.isMaximized()));

            writer.dataElement("content", contentAttributes);
        }

        // End contentManager
        writer.endElement("contentManager");
    }

    protected void saveBars(XMLWriter writer) throws SAXException {
        // Store model for bar
        writer.startElement("bars");

        saveBar(writer, ToolWindowAnchor.LEFT);
        saveBar(writer, ToolWindowAnchor.BOTTOM);
        saveBar(writer, ToolWindowAnchor.RIGHT);
        saveBar(writer, ToolWindowAnchor.TOP);

        writer.endElement("bars");
    }

    protected void saveBar(XMLWriter writer, ToolWindowAnchor anchor) throws SAXException {
        MultiSplitDockableContainer multiSplitDockableContainer = toolWindowManager.getBar(anchor).getToolsContainer();
        if (multiSplitDockableContainer.getContentCount() > 0) {
            AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(null, "anchor", null, null, anchor.toString());
            writer.startElement("bar", attributes);

            writer.startElement("model");

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(os);
            encoder.writeObject(multiSplitDockableContainer.getModel());
            encoder.flush();
            encoder.close();

            String model = os.toString();
            writer.cdata(model.substring(model.indexOf('\n')));

            writer.endElement("model");
            writer.endElement("bar");
        }
    }

    protected void parseElement(Element element, Object... args) {
        ElementParser elementParser = elementParserMap.get(element.getNodeName());
        if (elementParser == null || elementParser.parse(element, args)) {
            NodeList children = element.getChildNodes();
            for (int i = 0, size = children.getLength(); i < size; i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                    parseElement((Element) node, args);
            }
        }
    }


    public interface ElementParser {

        boolean parse(Element element, Object... args);

    }

    public abstract class ElementParserAdapter implements ElementParser {

        public Element getElement(Element root, String name) {
            NodeList list = root.getElementsByTagName(name);
            if (list.getLength() == 0)
                return null;
            return (Element) list.item(0);
        }

        public boolean getBoolean(Element element, String name, boolean defaultValue) {
            try {
                String attr = element.getAttribute(name);
                if (attr != null && !"".equals(attr.trim()))
                    return Boolean.parseBoolean(attr);
                else
                    return defaultValue;
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public int getInteger(Element element, String name, int defaultValue) {
            try {
                String attr = element.getAttribute(name);
                if (attr != null && !"".equals(attr.trim()))
                    return Integer.parseInt(attr);
                else
                    return defaultValue;
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public float getFloat(Element element, String name, float defaultValue) {
            try {
                String attr = element.getAttribute(name);
                if (attr != null && !"".equals(attr.trim()))
                    return Float.parseFloat(attr);
                else
                    return defaultValue;
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public boolean isAttributePresent(Element element, String name) {
            String attr = element.getAttribute(name);
            return attr != null && !"".equals(attr.trim());
        }
    }


    public class MyDoggyElementParser extends ElementParserAdapter {
        public boolean parse(Element element, Object... args) {
            // Validate version
            if (!"1.4.0".equals(element.getAttribute("version")))
                throw new IllegalArgumentException("Invalid workspace version. Expected 1.4.0");
            return true;
        }
    }

    public class ToolWindowManagerDescriptorElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Object... args) {
            ToolWindowManagerDescriptor descriptor = toolWindowManager.getToolWindowManagerDescriptor();
            descriptor.setNumberingEnabled(getBoolean(element, "numberingEnabled", true));
            descriptor.setPreviewEnabled(getBoolean(element, "previewEnabled", true));
            return true;
        }
    }

    public class DividerSizeElementParser extends ElementParserAdapter {
        public boolean parse(Element element, Object... args) {
            ToolWindowManagerDescriptor descriptor = toolWindowManager.getToolWindowManagerDescriptor();
            descriptor.setDividerSize(ToolWindowAnchor.LEFT, getInteger(element, "left", 5));
            descriptor.setDividerSize(ToolWindowAnchor.RIGHT, getInteger(element, "right", 5));
            descriptor.setDividerSize(ToolWindowAnchor.TOP, getInteger(element, "top", 5));
            descriptor.setDividerSize(ToolWindowAnchor.BOTTOM, getInteger(element, "bottom", 5));
            return true;
        }
    }

    public class AggregateModeElementParser extends ElementParserAdapter {
        public boolean parse(Element element, Object... args) {
            ToolWindowManagerDescriptor descriptor = toolWindowManager.getToolWindowManagerDescriptor();
            descriptor.setAggregateMode(ToolWindowAnchor.LEFT, getBoolean(element, "left", false));
            descriptor.setAggregateMode(ToolWindowAnchor.RIGHT, getBoolean(element, "right", false));
            descriptor.setAggregateMode(ToolWindowAnchor.TOP, getBoolean(element, "top", false));
            descriptor.setAggregateMode(ToolWindowAnchor.BOTTOM, getBoolean(element, "bottom", false));
            return true;
        }
    }

    public class PushAwayModeElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Object... args) {
            ToolWindowManagerDescriptor descriptor = toolWindowManager.getToolWindowManagerDescriptor();

            // Load mode settings
            NodeList modes = element.getElementsByTagName("mode");
            for (int i = 0, size = modes.getLength(); i < size; i++) {
                Element mode = (Element) modes.item(i);
                if ("MOST_RECENT".equals(mode.getAttribute("type"))) {
                    MostRecentDescriptor mostRecentDescriptor = (MostRecentDescriptor) descriptor.getPushAwayModeDescriptor(PushAwayMode.MOST_RECENT);

                    NodeList anchors = element.getElementsByTagName("anchor");
                    for (int j = 0, sizej = anchors.getLength(); j < sizej; j++) {
                        Element anchor = (Element) anchors.item(j);
                        mostRecentDescriptor.append(ToolWindowAnchor.valueOf(anchor.getAttribute("type")));
                    }
                }
            }

            // Setup pushAwayMode
            if (isAttributePresent(element, "pushAwayMode"))
                descriptor.setPushAwayMode(PushAwayMode.valueOf(element.getAttribute("pushAwayMode")));

            return false;
        }

    }

    public class ToolsElementParser extends ElementParserAdapter {
        protected MergePolicyApplier mergePolicyApplier;

        public boolean parse(Element element, Object... args) {
            mergePolicyApplier = (MergePolicyApplier) args[0];

            NodeList tools = element.getElementsByTagName("tool");

            for (int i = 0, size = tools.getLength(); i < size; i++) {
                Element tool = (Element) tools.item(i);

                // load descriptors
                ToolWindow toolWindow = toolWindowManager.getToolWindow(tool.getAttribute("id"));
                if (toolWindow == null)
                    continue;

                Element dockedType = getElement(tool, "docked");
                if (dockedType != null) {
                    DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
                    descriptor.setDockLength(getInteger(dockedType, "dockLength", 200));
                    descriptor.setPopupMenuEnabled(getBoolean(dockedType, "popupMenuEnabled", true));
                    descriptor.setAnimating(getBoolean(dockedType, "animating", true));
                    descriptor.setPreviewEnabled(getBoolean(dockedType, "previewEnabled", true));
                    descriptor.setPreviewDelay(getInteger(dockedType, "previewDelay", 0));
                    descriptor.setPreviewTransparentRatio(getFloat(dockedType, "previewTransparentRatio", 0.7f));
                    descriptor.setIdVisibleOnTitleBar(getBoolean(dockedType, "idVisibleOnTitleBar", true));
                    descriptor.setHideRepresentativeButtonOnVisible(
                            getBoolean(dockedType, "hideRepresentativeButtonOnVisible", false)
                    );
                }

                Element slidingType = getElement(tool, "sliding");
                if (slidingType != null) {
                    SlidingTypeDescriptor descriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
                    descriptor.setEnabled(getBoolean(slidingType, "enabled", true));
                    descriptor.setTransparentDelay(getInteger(slidingType, "transparentDelay", 0));
                    descriptor.setTransparentMode(getBoolean(slidingType, "transparentMode", true));
                    descriptor.setTransparentRatio(getFloat(slidingType, "transparentRatio", 0.7f));
                    descriptor.setAnimating(getBoolean(slidingType, "animating", true));
                    descriptor.setIdVisibleOnTitleBar(getBoolean(slidingType, "idVisibleOnTitleBar", true));
                }

                Element floatingType = getElement(tool, "floating");
                if (floatingType != null) {
                    FloatingTypeDescriptor descriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
                    descriptor.setEnabled(getBoolean(floatingType, "enabled", true));
                    descriptor.setTransparentDelay(getInteger(floatingType, "transparentDelay", 0));
                    descriptor.setTransparentMode(getBoolean(floatingType, "transparentMode", true));
                    descriptor.setTransparentRatio(getFloat(floatingType, "transparentRatio", 0.7f));
                    descriptor.setModal(getBoolean(floatingType, "modal", false));
                    descriptor.setAnimating(getBoolean(floatingType, "animating", true));
                    descriptor.setIdVisibleOnTitleBar(getBoolean(floatingType, "idVisibleOnTitleBar", true));

                    Element location = getElement(floatingType, "location");
                    if (location != null)
                        descriptor.setLocation(
                                getInteger(location, "x", 0),
                                getInteger(location, "y", 0)
                        );
                    Element dimension = getElement(floatingType, "size");
                    if (dimension != null)
                        descriptor.setSize(
                                getInteger(dimension, "width", 100),
                                getInteger(dimension, "height", 100)
                        );
                }

                Element floatingLiveType = getElement(tool, "floatingLive");
                if (floatingLiveType != null) {
                    FloatingLiveTypeDescriptor descriptor = (FloatingLiveTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
                    descriptor.setEnabled(getBoolean(floatingLiveType, "enabled", true));
                    descriptor.setTransparentDelay(getInteger(floatingLiveType, "transparentDelay", 0));
                    descriptor.setTransparentMode(getBoolean(floatingLiveType, "transparentMode", true));
                    descriptor.setTransparentRatio(getFloat(floatingLiveType, "transparentRatio", 0.7f));
                    descriptor.setAnimating(getBoolean(floatingLiveType, "animating", true));
                    descriptor.setIdVisibleOnTitleBar(getBoolean(floatingLiveType, "idVisibleOnTitleBar", true));

                    Element location = getElement(floatingType, "location");
                    if (location != null)
                        descriptor.setLocation(
                                getInteger(location, "x", 0),
                                getInteger(location, "y", 0)
                        );
                    Element dimension = getElement(floatingType, "size");
                    if (dimension != null)
                        descriptor.setSize(
                                getInteger(dimension, "width", 100),
                                getInteger(dimension, "height", 100)
                        );
                }

                ToolWindowType type = ToolWindowType.valueOf(tool.getAttribute("type"));
                if (type != ToolWindowType.EXTERN)
                    toolWindow.setType(type);

                toolWindow.setAutoHide(getBoolean(tool, "autoHide", false));
                toolWindow.setAvailable(getBoolean(tool, "available", false));
                int index = getInteger(tool, "index", -1);
                if (index != -1)
                    toolWindow.setIndex(index);
                toolWindow.setAggregateMode(getBoolean(tool, "aggregateMode", false));
                if (toolWindow.getType() != ToolWindowType.FLOATING_FREE)
                    toolWindow.setRepresentativeAnchorButtonVisible(
                            getBoolean(tool, "representativeAnchorButtonVisible", true)
                    );

                // Load tabs
                Element tabs = getElement(tool, "tabs");
                if (tabs != null) {
                    NodeList tabList = tabs.getElementsByTagName("tab");

                    // Compare tabLists
                    for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                        if (tab.getDockableDelegator() != null) {
                            String dockableId = tab.getDockableDelegator().getId();

                            for (int j = 0, sizej = tabList.getLength(); j < sizej; j++) {
                                Element tabElement = (Element) tabList.item(j);

                                if (dockableId.equals(tabElement.getAttribute("dockableId"))) {
                                    toolWindow.removeToolWindowTab(tab);
                                    break;
                                }
                            }
                        }
                    }

                    ToolWindowTab selectedTab = null;
                    for (int j = 0, sizej = tabList.getLength(); j < sizej; j++) {
                        Element tabElement = (Element) tabList.item(j);

                        String dockableId = tabElement.getAttribute("dockableId");
                        boolean selected = getBoolean(tabElement, "selected", false);

                        Dockable dockable = toolWindowManager.getDockable(dockableId);
                        if (dockable != null) {
                            ToolWindowTab tab = toolWindow.addToolWindowTab(dockable);
                            if (selected)
                                selectedTab = tab;
                            tab.setSelected(false);
                        }

                        if (selectedTab != null)
                            selectedTab.setSelected(true);
                    }
                } else {
                    for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                        if (tab.getDockableDelegator() != null)
                            toolWindow.removeToolWindowTab(tab);
                    }
                }
            }

            apply(tools, ToolWindowAnchor.LEFT);
            apply(tools, ToolWindowAnchor.BOTTOM);
            apply(tools, ToolWindowAnchor.RIGHT);
            apply(tools, ToolWindowAnchor.TOP);
            return false;
        }

        protected void apply(NodeList tools, ToolWindowAnchor anchor) {
            // Filter tools by anchor
            java.util.List<Element> toolsByAnchor = new ArrayList<Element>();
            for (int i = 0, size = tools.getLength(); i < size; i++) {
                Element tool = (Element) tools.item(i);
                if (ToolWindowAnchor.valueOf(tool.getAttribute("anchor")) == anchor)
                    toolsByAnchor.add(tool);
            }

            Collections.sort(toolsByAnchor, new Comparator<Element>() {
                public int compare(Element o1, Element o2) {
                    int anchorIndex1 = getInteger(o1, "anchorIndex", 0);
                    int anchorIndex2 = getInteger(o2, "anchorIndex", 0);

                    if (anchorIndex1 < anchorIndex2)
                        return -1;
                    else if (anchorIndex1 == anchorIndex2)
                        return 0;
                    return 1;
                }
            });

            ToolWindow activeTool = null;
            ToolWindow maximizedTool = null;
            for (Element tool : toolsByAnchor) {
                ToolWindow toolWindow = toolWindowManager.getToolWindow(tool.getAttribute("id"));
                if (toolWindow == null)
                    continue;

                int anchorIndex = getInteger(tool, "anchorIndex", Integer.MIN_VALUE);
                ToolWindowAnchor toolWindowAnchor = ToolWindowAnchor.LEFT;
                if (isAttributePresent(tool, "anchor"))
                    toolWindowAnchor = ToolWindowAnchor.valueOf(tool.getAttribute("anchor"));

                if (anchorIndex == Integer.MIN_VALUE)
                    toolWindow.setAnchor(toolWindowAnchor);
                else
                    toolWindow.setAnchor(toolWindowAnchor,
                                         anchorIndex);

                mergePolicyApplier.applyToolWindow(toolWindow, tool);

                if (getBoolean(tool, "active", false))
                    activeTool = toolWindow;

                if (getBoolean(tool, "maximized", false))
                    maximizedTool = toolWindow;
            }

            if (activeTool != null)
                activeTool.setActive(true);

            if (maximizedTool != null)
                maximizedTool.setMaximized(true);

        }
    }

    public class ContentManagerElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Object... args) {
            NodeList contents = element.getElementsByTagName("content");

            Content selectedContent = null;
            Content maximizedContent = null;

            for (int i = 0, size = contents.getLength(); i < size; i++) {
                Element contentElement = (Element) contents.item(i);
                Content content = toolWindowManager.getContentManager().getContent(contentElement.getAttribute("id"));

                if (content != null) {
                    if (getBoolean(contentElement, "selected", false))
                        selectedContent = content;
                    if (getBoolean(contentElement, "maximized", false))
                        maximizedContent = content;


                    content.setEnabled(getBoolean(contentElement, "enabled", true));
                    content.setDetached(getBoolean(contentElement, "detached", false));

                    content.setMaximized(false);
                }
            }

            if (selectedContent != null)
                selectedContent.setSelected(true);

            if (maximizedContent != null)
                maximizedContent.setMaximized(true);

            return false;
        }
    }

    public class BarElementParser extends ElementParserAdapter {

        public boolean parse(Element element, Object... args) {
            ToolWindowAnchor anchor = ToolWindowAnchor.valueOf(element.getAttribute("anchor"));

            Element modelElement = getElement(element, "model");
            if (modelElement != null) {
                String text = modelElement.getTextContent();
                XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(text.getBytes()));
                MultiSplitLayout.Node model = (MultiSplitLayout.Node) decoder.readObject();

                toolWindowManager.getBar(anchor).getToolsContainer().setModel(model);
            }

            return false;
        }

    }
}