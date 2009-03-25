package org.noos.xing.mydoggy.plaf.ui.drag;

import java.awt.datatransfer.DataFlavor;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyDataFlavor extends DataFlavor {

    public MyDoggyDataFlavor() {
    }

    public MyDoggyDataFlavor(Class<?> representationClass, String humanPresentableName) {
        super(representationClass, humanPresentableName);
    }

    public MyDoggyDataFlavor(String mimeType, String humanPresentableName) {
        super(mimeType, humanPresentableName);
    }

    public MyDoggyDataFlavor(String mimeType, String humanPresentableName, ClassLoader classLoader) throws ClassNotFoundException {
        super(mimeType, humanPresentableName, classLoader);
    }

    public MyDoggyDataFlavor(String mimeType) throws ClassNotFoundException {
        super(mimeType);
    }

    @Override
    public boolean equals(DataFlavor that) {
        if (!super.equals(that))
            return false;

        if (getHumanPresentableName() == null) {
            if (that.getRepresentationClass() != null) {
                return false;
            }
        } else {
            if (!getHumanPresentableName().equals(that.getHumanPresentableName())) {
                return false;
            }
        }
        return true;

    }
}
