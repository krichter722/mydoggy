package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XmlPersistenceDelegate implements PersistenceDelegate {
    private ToolWindowManager toolWindowManager;

    public XmlPersistenceDelegate(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public void save(OutputStream outputStream) {
        try {
            XMLWriter writer = new XMLWriter(new OutputStreamWriter(outputStream));

            writer.startDocument();

            AttributesImpl mydoggyAttributes = new AttributesImpl();
            mydoggyAttributes.addAttribute(null, "version", null, null, "1.3.0");
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

            writer.endElement("mydoggy");
            writer.endDocument();

            writer.flush();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToolWindowManagerDescriptor(XMLWriter writer) throws SAXException{
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
    }

    public void apply(InputStream inputStream) {
        merge(inputStream, MergePolicy.RESET);
    }

    public void merge(InputStream inputStream, MergePolicy mergePolicy) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream, new MyDoggyHandler(toolWindowManager, mergePolicy));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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
        writer.dataElement("docked", dockedDescriptorAttributes);

        // DockedTypeDescriptor
        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
        AttributesImpl slidingDescriptorAttributes = new AttributesImpl();
        slidingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(slidingTypeDescriptor.isTransparentMode()));
        slidingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(slidingTypeDescriptor.getTransparentDelay()));
        slidingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(slidingTypeDescriptor.getTransparentRatio()));
        slidingDescriptorAttributes.addAttribute(null, "enabled", null, null, String.valueOf(slidingTypeDescriptor.isEnabled()));
        slidingDescriptorAttributes.addAttribute(null, "animating", null, null, String.valueOf(slidingTypeDescriptor.isAnimating()));
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

        writer.endElement("descriptors");
        writer.endElement("tool");
    }
}