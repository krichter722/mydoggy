package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
            mydoggyAttributes.addAttribute(null, "version", null, null, "1.0");
            writer.startElement("mydoggy", mydoggyAttributes);

            writer.startElement("tools");
            for (ToolWindow toolWindow : toolWindowManager.getToolWindows()) {

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
                floatingDescriptorAttributes.addAttribute(null, "location", null, null, String.valueOf(floatingTypeDescriptor.getLocation()));
                floatingDescriptorAttributes.addAttribute(null, "size", null, null, String.valueOf(floatingTypeDescriptor.getSize()));
                floatingDescriptorAttributes.addAttribute(null, "modal", null, null, String.valueOf(floatingTypeDescriptor.isModal()));
                floatingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(floatingTypeDescriptor.isTransparentMode()));
                floatingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(floatingTypeDescriptor.getTransparentDelay()));
                floatingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(floatingTypeDescriptor.getTransparentRatio()));
                floatingDescriptorAttributes.addAttribute(null, "enabled", null, null, String.valueOf(floatingTypeDescriptor.isEnabled()));
                writer.dataElement("floating", floatingDescriptorAttributes);

                writer.endElement("descriptors");
                writer.endElement("tool");
            }
            writer.endElement("tools");

            writer.endElement("mydoggy");
            writer.endDocument();
            writer.flush();
        } catch (SAXException e) {
            e.printStackTrace();  //TODO To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void apply(InputStream inputStream) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream, new XmlHandler(toolWindowManager));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
