package org.noos.xing.mydoggy.itest.impl.xml;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.noos.xing.mydoggy.itest.impl.ListInteractiveTestRunner;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XMLInteractiveTestRunner extends ListInteractiveTestRunner {
    private String testResource;

    public XMLInteractiveTestRunner(String testResource) {
        this.testResource = testResource;
    }

    public void run() {
        // Load tests
        SAXBuilder saxBuilder = new SAXBuilder();
        Document testDoc = null;
        try {
            URL url = null;

            File f = new File(testResource);
            if (f.exists())
                url = f.toURL();
            else {
                url = this.getClass().getClassLoader().getResource(testResource);
            }

            testDoc = saxBuilder.build(url);
	    } catch (Exception e) {
            throw new RuntimeException("Error loading "+ testResource +". Error: "+e.getMessage(), e);
	    }

        List children = testDoc.getRootElement().getChildren();
        for (Object child : children) {
            addInteractiveTest(new XMLInteractiveTest((Element) child));
        }

        // Execute list tests
        super.run();
    }
}