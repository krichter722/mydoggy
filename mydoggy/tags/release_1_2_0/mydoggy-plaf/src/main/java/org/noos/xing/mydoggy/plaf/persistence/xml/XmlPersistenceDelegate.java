package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.awt.*;

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
            mydoggyAttributes.addAttribute(null, "version", null, null, "1.2.0");
            mydoggyAttributes.addAttribute(null, "pushAwayMode", null, null, 
                                           toolWindowManager.getToolWindowManagerDescriptor().getPushAwayMode().toString());
            writer.startElement("mydoggy", mydoggyAttributes);
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

    public void apply(InputStream inputStream) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream, new MyDoggyHandler(toolWindowManager));
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
        writer.startElement("tool", toolAttributes);

        writer.startElement("descriptors");

        // DockedTypeDescriptor
        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        AttributesImpl dockedDescriptorAttributes = new AttributesImpl();
        dockedDescriptorAttributes.addAttribute(null, "dockLength", null, null, String.valueOf(dockedTypeDescriptor.getDockLength()));
        dockedDescriptorAttributes.addAttribute(null, "popupMenuEnabled", null, null, String.valueOf(dockedTypeDescriptor.isPopupMenuEnabled()));
        writer.dataElement("docked", dockedDescriptorAttributes);

        // DockedTypeDescriptor
        SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
        AttributesImpl slidingDescriptorAttributes = new AttributesImpl();
        slidingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(slidingTypeDescriptor.isTransparentMode()));
        slidingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(slidingTypeDescriptor.getTransparentDelay()));
        slidingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(slidingTypeDescriptor.getTransparentRatio()));
        slidingDescriptorAttributes.addAttribute(null, "enabled", null, null, String.valueOf(slidingTypeDescriptor.isEnabled()));
        writer.dataElement("sliding", slidingDescriptorAttributes);

        // FloatingTypeDescriptor
        FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
        AttributesImpl floatingDescriptorAttributes = new AttributesImpl();
        floatingDescriptorAttributes.addAttribute(null, "modal", null, null, String.valueOf(floatingTypeDescriptor.isModal()));
        floatingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(floatingTypeDescriptor.isTransparentMode()));
        floatingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(floatingTypeDescriptor.getTransparentDelay()));
        floatingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(floatingTypeDescriptor.getTransparentRatio()));
        floatingDescriptorAttributes.addAttribute(null, "enabled", null, null, String.valueOf(floatingTypeDescriptor.isEnabled()));

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
