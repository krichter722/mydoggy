package org.noos.xing.mydoggy.itest.impl.xml;

import org.jdom.Element;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class XMLInteractiveTest implements InteractiveTest {
    private Element itestElement;

    protected XMLInteractiveTest(Element itestElement) {
        this.itestElement = itestElement;
    }

    public Container setup() {
        return null;
    }

    public void dispose() {

    }

    public final void interactiveTest(InteractiveUI interactiveUI) {

    }
    
}
