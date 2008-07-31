package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Content;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ContentWindow {

    void addContent(Content content, Component contentComponent);

    void addContent(Content content, Component componentContent,
                    Content aggregationOnContent, AggregationPosition aggregationPosition);

    void removeContent(Content content);

    boolean containsContent(Content content);

    int getNumContents();

}
