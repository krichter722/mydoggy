package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.plaf.ui.look.ContentPanelUI;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentPanel extends JPanel {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ContentPanelUI";


    protected TableLayout layout;

    protected String parentPrefix;
    protected int threshold;


    public ContentPanel(String parentPrefix) {
        this(parentPrefix, 20);
    }

    public ContentPanel(String parentPrefix, int threshold) {
        this.parentPrefix = parentPrefix;
        this.threshold = threshold;
        setLayout(layout = new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));

        updateUI();
    }


    @Override
    public void paint(Graphics g) {
        if (this.isOpaque()) {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        super.paint(g);

        paintComponent(g);
   }

    public void updateUI() {
        if (parentPrefix != null)
            setUI((ContentPanelUI) UIManager.getUI(this));
    }

    public String getUIClassID() {
        return uiClassID;
    }

    public void setUI(ContentPanelUI ui) {
        super.setUI(ui);
    }


    public String getParentPrefix() {
        return parentPrefix;
    }

    public int getThreshold() {
        return threshold;
    }

    public Component getComponent() {
        return (getComponentCount() == 0) ? null : getComponent(0);
    }

    public void setComponent(Component component) {
        removeAll();
        add(component, "1,1,FULL,FULL");
    }

    public void resetComponent() {
        removeAll();
        resetLayout();        
    }

    public void resetLayout() {
        layout.setColumn(0, 0);
        layout.setColumn(2, 0);
        layout.setRow(0, 0);
        layout.setRow(2, 0);
    }


}
