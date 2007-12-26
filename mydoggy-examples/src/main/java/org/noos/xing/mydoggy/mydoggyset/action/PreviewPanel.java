package org.noos.xing.mydoggy.mydoggyset.action;

import info.clearthought.layout.TableLayout;
import org.noos.xing.yasaf.plaf.component.JImage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PreviewPanel extends JPanel {
    protected JImage image;

    protected SpinnerModel widthModel;
    protected SpinnerModel heightModel;


    public PreviewPanel() {
        super(new TableLayout(new double[][]{{-2, -1}, {3, 20, 3, 20, 3, -1}}));

        setBorder(new TitledBorder("Preview"));

        add(image = new JImage(), "0,5,1,5,FULL,FULL");
        image.setPreferredSize(new Dimension(150, 113));

        add(new JLabel("Width :"), "0,1,FULL,FULL");
        add(new JSpinner(widthModel = new SpinnerNumberModel()), "1,1,FULL,FULL");
        add(new JLabel("Height :"), "0,3,FULL,FULL");
        add(new JSpinner(heightModel = new SpinnerNumberModel()), "1,3,FULL,FULL");
    }


    public void setImage(BufferedImage bufferedImage) {
        image.setImage(bufferedImage);

        widthModel.setValue(bufferedImage.getWidth());
        heightModel.setValue(bufferedImage.getHeight());
    }

    public int getImageWidth() {
        return (Integer) widthModel.getValue();
    }

    public int getImageHeight() {
        return (Integer) heightModel.getValue();
    }

    public void setImageWidth(int width) {
        widthModel.setValue(width);
    }

    public void setImageHeight(int height) {
        heightModel.setValue(height);
    }


    public BufferedImage getImage() {
        BufferedImage bufferedImage = image.getImage();
        if (bufferedImage.getWidth() == getImageWidth() && bufferedImage.getHeight() == getImageHeight())
            return bufferedImage;
        else {
            Image scaledImage = bufferedImage.getScaledInstance(getImageWidth(), getImageHeight(), Image.SCALE_SMOOTH);
            BufferedImage scaled = new BufferedImage(getImageWidth(), getImageHeight(), BufferedImage.TYPE_INT_RGB);
            scaled.getGraphics().drawImage(scaledImage, 0, 0, null);

            return scaled;
        }
    }
}
