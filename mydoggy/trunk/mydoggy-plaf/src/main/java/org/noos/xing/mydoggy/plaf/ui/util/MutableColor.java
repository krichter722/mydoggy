package org.noos.xing.mydoggy.plaf.ui.util;

import sun.awt.image.IntegerComponentRaster;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MutableColor extends Color {
    
    private static final double FACTOR = 0.7;

    private int value;
    private float frgbvalue[] = null;
    private float fvalue[] = null;
    private float falpha = 0.0f;
    private ColorSpace cs = null;

    public MutableColor(int r, int g, int b) {
        super(r, g, b);
        value = ((255 & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF));
        testColorValueRange(r, g, b, 255);
    }

    public MutableColor(Color c) {
        this(c.getRed(), c.getGreen(), c.getBlue());
    }

    public int getRed() {
        return (getRGB() >> 16) & 0xFF;
    }

    public int getGreen() {
        return (getRGB() >> 8) & 0xFF;
    }

    public int getBlue() {
        return (getRGB() >> 0) & 0xFF;
    }

    public int getAlpha() {
        return (getRGB() >> 24) & 0xff;
    }

    public int getRGB() {
        return value;
    }

    public Color brighter() {
        int r = getRed();
        int g = getGreen();
        int b = getBlue();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int) (1.0 / (1.0 - FACTOR));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new Color(Math.min((int) (r / FACTOR), 255),
                         Math.min((int) (g / FACTOR), 255),
                         Math.min((int) (b / FACTOR), 255));
    }

    public Color darker() {
        return new Color(Math.max((int) (getRed() * FACTOR), 0),
                         Math.max((int) (getGreen() * FACTOR), 0),
                         Math.max((int) (getBlue() * FACTOR), 0));
    }

    public int hashCode() {
        return value;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MutableColor)
            return ((MutableColor) obj).value == this.value;
        else if (obj instanceof Color) {
            Color c = (Color) obj;
            return getRed() == c.getRed() && getBlue() == c.getBlue() && getGreen() == c.getGreen() &&
                   getAlpha() == c.getAlpha();
        }
        return false;
    }

    public String toString() {
        return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "]";
    }

    public float[] getRGBComponents(float[] compArray) {
        float[] f;
        if (compArray == null) {
            f = new float[4];
        } else {
            f = compArray;
        }
        if (frgbvalue == null) {
            f[0] = ((float) getRed()) / 255f;
            f[1] = ((float) getGreen()) / 255f;
            f[2] = ((float) getBlue()) / 255f;
            f[3] = ((float) getAlpha()) / 255f;
        } else {
            f[0] = frgbvalue[0];
            f[1] = frgbvalue[1];
            f[2] = frgbvalue[2];
            f[3] = falpha;
        }
        return f;
    }

    public float[] getRGBColorComponents(float[] compArray) {
        float[] f;
        if (compArray == null) {
            f = new float[3];
        } else {
            f = compArray;
        }
        if (frgbvalue == null) {
            f[0] = ((float) getRed()) / 255f;
            f[1] = ((float) getGreen()) / 255f;
            f[2] = ((float) getBlue()) / 255f;
        } else {
            f[0] = frgbvalue[0];
            f[1] = frgbvalue[1];
            f[2] = frgbvalue[2];
        }
        return f;
    }

    public float[] getComponents(float[] compArray) {
        if (fvalue == null)
            return getRGBComponents(compArray);
        float[] f;
        int n = fvalue.length;
        if (compArray == null) {
            f = new float[n + 1];
        } else {
            f = compArray;
        }
        System.arraycopy(fvalue, 0, f, 0, n);
        f[n] = falpha;
        return f;
    }

    public float[] getColorComponents(float[] compArray) {
        if (fvalue == null)
            return getRGBColorComponents(compArray);
        float[] f;
        int n = fvalue.length;
        if (compArray == null) {
            f = new float[n];
        } else {
            f = compArray;
        }
        System.arraycopy(fvalue, 0, f, 0, n);
        return f;
    }

    public float[] getComponents(ColorSpace cspace, float[] compArray) {
        if (cs == null) {
            cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        }
        float f[];
        if (fvalue == null) {
            f = new float[3];
            f[0] = ((float) getRed()) / 255f;
            f[1] = ((float) getGreen()) / 255f;
            f[2] = ((float) getBlue()) / 255f;
        } else {
            f = fvalue;
        }
        float tmp[] = cs.toCIEXYZ(f);
        float tmpout[] = cspace.fromCIEXYZ(tmp);
        if (compArray == null) {
            compArray = new float[tmpout.length + 1];
        }
        System.arraycopy(tmpout, 0, compArray, 0, tmpout.length);
        if (fvalue == null) {
            compArray[tmpout.length] = ((float) getAlpha()) / 255f;
        } else {
            compArray[tmpout.length] = falpha;
        }
        return compArray;
    }

    public float[] getColorComponents(ColorSpace cspace, float[] compArray) {
        if (cs == null) {
            cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        }
        float f[];
        if (fvalue == null) {
            f = new float[3];
            f[0] = ((float) getRed()) / 255f;
            f[1] = ((float) getGreen()) / 255f;
            f[2] = ((float) getBlue()) / 255f;
        } else {
            f = fvalue;
        }
        float tmp[] = cs.toCIEXYZ(f);
        float tmpout[] = cspace.fromCIEXYZ(tmp);
        if (compArray == null) {
            return tmpout;
        }
        System.arraycopy(tmpout, 0, compArray, 0, tmpout.length);
        return compArray;
    }

    public ColorSpace getColorSpace() {
        if (cs == null) {
            cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        }
        return cs;
    }

    transient private PaintContext theContext;

    public synchronized PaintContext createContext(ColorModel cm, Rectangle r,
                                                   Rectangle2D r2d,
                                                   AffineTransform xform,
                                                   RenderingHints hints) {
        PaintContext pc = theContext;
        if (pc == null) {
            pc = new ColorPaintContext(value, cm);
            theContext = pc;
        }
        return pc;
    }

    public int getTransparency() {
        int alpha = getAlpha();
        if (alpha == 0xff) {
            return Transparency.OPAQUE;
        } else if (alpha == 0) {
            return Transparency.BITMASK;
        } else {
            return Transparency.TRANSLUCENT;
        }
    }


    public void setRGB(int r, int g, int b) {
        value = ((255 & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF) << 0);

        frgbvalue = null;
        fvalue = null;
        falpha = 0.0f;
        cs = null;
    }

    public void setRGB(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        value = ((255 & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF) << 0);

        frgbvalue = null;
        fvalue = null;
        falpha = 0.0f;
        cs = null;
    }


    private static void testColorValueRange(int r, int g, int b, int a) {
        boolean rangeError = false;
        String badComponentString = "";

        if (a < 0 || a > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Alpha";
        }
        if (r < 0 || r > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Red";
        }
        if (g < 0 || g > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Green";
        }
        if (b < 0 || b > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Blue";
        }
        if (rangeError) {
            throw new IllegalArgumentException("Color parameter outside of expected range:"
                                               + badComponentString);
        }
    }

    static class ColorPaintContext implements PaintContext {
        int color;
        WritableRaster savedTile;

        protected ColorPaintContext(int color, ColorModel cm) {
            this.color = color;
        }

        public void dispose() {
        }

        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        public synchronized Raster getRaster(int x, int y, int w, int h) {
            WritableRaster t = savedTile;

            if (t == null || w > t.getWidth() || h > t.getHeight()) {
                t = getColorModel().createCompatibleWritableRaster(w, h);
                IntegerComponentRaster icr = (IntegerComponentRaster) t;
                Arrays.fill(icr.getDataStorage(), color);
                if (w <= 64 && h <= 64) {
                    savedTile = t;
                }
            }

            return t;
        }
    }

}
