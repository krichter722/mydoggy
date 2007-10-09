package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.persistence.merge.MergePolicyApplier;
import org.noos.xing.mydoggy.plaf.persistence.merge.ResetMergePolicy;
import org.noos.xing.mydoggy.plaf.persistence.merge.UnionMergePolicy;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XMLPersistenceDelegate implements PersistenceDelegate {
    protected ToolWindowManager toolWindowManager;
    protected Map<MergePolicy, MergePolicyApplier> mergePolicyApplierMap;

    public XMLPersistenceDelegate(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
        initMergePolicyApplierMap();
    }

    public void save(OutputStream outputStream) {
        try {
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(outputStream));

            writer.startDocument();

            AttributesImpl mydoggyAttributes = new AttributesImpl();
            mydoggyAttributes.addAttribute(null, "version", null, null, "1.3.2");
            mydoggyAttributes.addAttribute(null, "pushAwayMode", null, null, 
                                           toolWindowManager.getToolWindowManagerDescriptor().getPushAwayMode().toString());
            writer.startElement("mydoggy", mydoggyAttributes);

            // Store PushAway Pref
            saveToolWindowManagerDescriptor(writer);

            // Store tools pref.
            writer.startElement("tools");
            for (ToolWindow toolWindow : toolWindowManager.getToolWindows())
                saveToolWindow(writer, toolWindow);
            writer.endElement("tools");

            saveContentManager(writer);

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
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream, new XMLReaderHandler(toolWindowManager,
                                                              mergePolicyApplierMap.get(mergePolicy)));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    protected void initMergePolicyApplierMap() {
        mergePolicyApplierMap = new HashMap<MergePolicy, MergePolicyApplier>();
        mergePolicyApplierMap.put(PersistenceDelegate.MergePolicy.RESET, new ResetMergePolicy());
        mergePolicyApplierMap.put(PersistenceDelegate.MergePolicy.UNION, new UnionMergePolicy());
    }

    protected void saveToolWindow(XMLWriter writer, ToolWindow toolWindow) throws SAXException {
        AttributesImpl toolAttributes = new AttributesImpl();
        toolAttributes.addAttribute(null, "id", null, null, String.valueOf(toolWindow.getId()));
        toolAttributes.addAttribute(null, "available", null, null, String.valueOf(toolWindow.isAvailable()));
        toolAttributes.addAttribute(null, "visible", null, null, String.valueOf(toolWindow.isVisible()));
        toolAttributes.addAttribute(null, "active", null, null, String.valueOf(toolWindow.isActive()));
        toolAttributes.addAttribute(null, "autoHide", null, null, String.valueOf(toolWindow.isAutoHide()));
        toolAttributes.addAttribute(null, "anchor", null, null, String.valueOf(toolWindow.getAnchor()));
        toolAttributes.addAttribute(null, "type", null, null, String.valueOf(toolWindow.getType()));
        toolAttributes.addAttribute(null, "aggregateMode", null, null, String.valueOf(toolWindow.isAggregateMode()));
        toolAttributes.addAttribute(null, "maximized", null, null, String.valueOf(toolWindow.isMaximized()));
        toolAttributes.addAttribute(null, "index", null, null, String.valueOf(toolWindow.getIndex()));
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
        // FloatingTypeDescriptor
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


        writer.endElement("descriptors");

        boolean addTabsTag = false;
        for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
            if (tab.getToolWindow() != null) {
                if (!addTabsTag) {
                    addTabsTag = true;
                    writer.startElement("tabs");
                }
                AttributesImpl attributes = new AttributesImpl();
                attributes.addAttribute(null, "toolWindowId", null, null, tab.getToolWindow().getId());
                writer.dataElement("tab", attributes);
            }
        }

        if (addTabsTag)
            writer.endElement("tabs");

        writer.endElement("tool");
    }

    protected void saveToolWindowManagerDescriptor(XMLWriter writer) throws SAXException{
        AttributesImpl twdmAttributes = new AttributesImpl();
        ToolWindowManagerDescriptor descriptor = toolWindowManager.getToolWindowManagerDescriptor();
        twdmAttributes.addAttribute(null, "dividerLeft", null, null, String.valueOf(descriptor.getDividerSize(ToolWindowAnchor.LEFT)));
        twdmAttributes.addAttribute(null, "dividerRight", null, null, String.valueOf(descriptor.getDividerSize(ToolWindowAnchor.RIGHT)));
        twdmAttributes.addAttribute(null, "dividerTop", null, null, String.valueOf(descriptor.getDividerSize(ToolWindowAnchor.TOP)));
        twdmAttributes.addAttribute(null, "dividerBottom", null, null, String.valueOf(descriptor.getDividerSize(ToolWindowAnchor.BOTTOM)));
        twdmAttributes.addAttribute(null, "numberingEnabled", null, null, String.valueOf(descriptor.isNumberingEnabled()));
        writer.startElement("toolWindowDescriptorManager", twdmAttributes);

        // Start pushAway
        writer.startElement("pushAway");

        // start MOST_RECENT policy
        AttributesImpl policyAttributes = new AttributesImpl();
        policyAttributes.addAttribute(null, "type", null, null, String.valueOf(PushAwayMode.MOST_RECENT));
        writer.startElement("mode", policyAttributes);

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

        writer.endElement("toolWindowDescriptorManager");
    }

    protected void saveContentManager(XMLWriter writer) throws SAXException {
        // Start contentManager
        writer.startElement("contentManager");

        for (Content content : toolWindowManager.getContentManager().getContents()) {

            AttributesImpl contentAttributes = new AttributesImpl();
            contentAttributes.addAttribute(null, "key", null, null, String.valueOf(content.getKey()));
            contentAttributes.addAttribute(null, "detached", null, null, String.valueOf(content.isDetached()));
            contentAttributes.addAttribute(null, "enabled", null, null, String.valueOf(content.isEnabled()));
            contentAttributes.addAttribute(null, "selected", null, null, String.valueOf(content.isSelected()));

            writer.dataElement("content", contentAttributes);
        }

        // End contentManager
        writer.endElement("contentManager");
    }
}
