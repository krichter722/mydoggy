package org.noos.xing.mydoggy.plaf.persistence;

import org.noos.xing.mydoggy.PersistenceDelegate;
import org.noos.xing.mydoggy.ToolWindowManager;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XmlPersistenceDelegate implements PersistenceDelegate {
    private ToolWindowManager toolWindowManager;

    public XmlPersistenceDelegate(ToolWindowManager toolWindowManager) {
        this.toolWindowManager = toolWindowManager;
    }

    public void save(OutputStream outputStream) {
        // TODO: to be continued...
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
