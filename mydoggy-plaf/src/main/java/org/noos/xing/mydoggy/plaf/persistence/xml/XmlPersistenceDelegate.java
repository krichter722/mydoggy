package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.noos.xing.mydoggy.*;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

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

            writer.startElement("tools");
            for (ToolWindow toolWindow : toolWindowManager.getToolWindows()) {

                AttributesImpl toolAttributes = new AttributesImpl();
                toolAttributes.addAttribute(null, "available", null, null, String.valueOf(toolWindow.isAvailable()));
                toolAttributes.addAttribute(null, "visible", null, null, String.valueOf(toolWindow.isVisible()));
                toolAttributes.addAttribute(null, "active", null, null, String.valueOf(toolWindow.isActive()));
                toolAttributes.addAttribute(null, "autoHide", null, null, String.valueOf(toolWindow.isAutoHide()));
                writer.startElement("tool", toolAttributes);

                // DockedTypeDescriptor
                DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
                AttributesImpl dockedDescriptorAttributes = new AttributesImpl();
                dockedDescriptorAttributes.addAttribute(null, "dockLength", null, null, String.valueOf(dockedTypeDescriptor.getDockLength()));
                dockedDescriptorAttributes.addAttribute(null, "popupMenuEnabled", null, null, String.valueOf(dockedTypeDescriptor.isPopupMenuEnabled()));
                writer.dataElement("dockedDescriptor", dockedDescriptorAttributes, null);

                // DockedTypeDescriptor
                SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.SLIDING);
                AttributesImpl slidingDescriptorAttributes = new AttributesImpl();
                slidingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(slidingTypeDescriptor.isTransparentMode()));
                slidingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(slidingTypeDescriptor.getTransparentDelay()));
                slidingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(slidingTypeDescriptor.getTransparentRatio()));
                writer.dataElement("slidingDescriptor", slidingDescriptorAttributes, null);

                // FloatingTypeDescriptor
                FloatingTypeDescriptor floatingTypeDescriptor = (FloatingTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.FLOATING);
                AttributesImpl floatingDescriptorAttributes = new AttributesImpl();
                floatingDescriptorAttributes.addAttribute(null, "location", null, null, String.valueOf(floatingTypeDescriptor.getLocation()));
                floatingDescriptorAttributes.addAttribute(null, "size", null, null, String.valueOf(floatingTypeDescriptor.getSize()));
                floatingDescriptorAttributes.addAttribute(null, "modal", null, null, String.valueOf(floatingTypeDescriptor.isModal()));
                floatingDescriptorAttributes.addAttribute(null, "transparentMode", null, null, String.valueOf(floatingTypeDescriptor.isTransparentMode()));
                floatingDescriptorAttributes.addAttribute(null, "transparentDelay", null, null, String.valueOf(floatingTypeDescriptor.getTransparentDelay()));
                floatingDescriptorAttributes.addAttribute(null, "transparentRatio", null, null, String.valueOf(floatingTypeDescriptor.getTransparentRatio()));
                writer.dataElement("floatingDescriptor", floatingDescriptorAttributes, null);

                writer.endElement("tool");
            }
            writer.endElement("tools");

            writer.endDocument();
            writer.flush();
        } catch (SAXException e) {
            e.printStackTrace();  //TODO To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void load(InputStream inputStream) {
        // TODO: to be continued...
    }

    private static String quoteCharacters(String s) {
	StringBuffer result = null;
        for(int i = 0, max = s.length(), delta = 0; i < max; i++) {
	    char c = s.charAt(i);
	    String replacement = null;

	    if (c == '&') {
		replacement = "&amp;";
	    } else if (c == '<') {
		replacement = "&lt;";
	    } else if (c == '\r') {
		replacement = "&#13;";
	    } else if (c == '>') {
		replacement = "&gt;";
	    } else if (c == '"') {
		replacement = "&quot;";
	    } else if (c == '\'') {
		replacement = "&apos;";
	    }

	    if (replacement != null) {
		if (result == null) {
		    result = new StringBuffer(s);
		}
		result.replace(i + delta, i + delta + 1, replacement);
		delta += (replacement.length() - 1);
	    }
        }
        if (result == null) {
            return s;
        }
	return result.toString();
    }

    private void writeln(String exp) {
        /*try {
            for(int i = 0; i < indentation; i++) {
                out.write(' ');
            }
            out.write(exp.getBytes(encoding));
            out.write(" \n".getBytes(encoding));
        }
        catch (IOException e) {
            getExceptionListener().exceptionThrown(e);
        } */
    }

}
