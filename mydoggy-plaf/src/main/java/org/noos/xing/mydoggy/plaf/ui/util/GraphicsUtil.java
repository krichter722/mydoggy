package org.noos.xing.mydoggy.plaf.ui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Angelo De Caro
 */
public class GraphicsUtil {

    public static final int FROM_CENTRE_GRADIENT_ON_Y = 0;
    public static final int FROM_CENTRE_GRADIENT_ON_X = 1;

    public static final int LEFT_TO_RIGHT_GRADIENT = 2;
    public static final int RIGHT_TO_LEFT_GRADIENT = 3;
    public static final int BOTTOM_TO_UP_GRADIENT = 4;
    public static final int UP_TO_BOTTOM_GRADIENT = 5;

    public static void fillRect(Graphics g, JComponent c, Color start, Color end) {
        if (c.isOpaque())
            fillRect(g, c.getBounds(), start, end, null, FROM_CENTRE_GRADIENT_ON_Y);
    }

    public static void fillRect(Graphics g, Rectangle r, Color start, Color end) {
        fillRect(g, r, start, end, null, FROM_CENTRE_GRADIENT_ON_Y);
    }

    public static void fillRect(Graphics g, Rectangle r, Color start, Color end, Shape clip, int direction) {
        switch (direction) {
            case LEFT_TO_RIGHT_GRADIENT:
                fillRect(g, r, r.x, r.y, start, r.x + r.width, r.y, end, clip);
                break;
            case RIGHT_TO_LEFT_GRADIENT:
                fillRect(g, r, r.x + r.width, r.y, start, r.x, r.y, end, clip);
                break;
            case BOTTOM_TO_UP_GRADIENT:
                fillRect(g, r, r.x, r.y + r.height, start, r.x, r.y, end, clip);
                break;
            case UP_TO_BOTTOM_GRADIENT:
                fillRect(g, r, r.x, r.y, start, r.x, r.y + r.height, end, clip);
                break;

            case FROM_CENTRE_GRADIENT_ON_X:
                Rectangle tmp = new Rectangle(r);
                tmp.width = tmp.width >> 1;
                fillRect(g, tmp, r.x, r.y, start, r.x + r.width, r.y, end, clip);

                if (r.width % 2 == 0) {
                    tmp = new Rectangle(r);
                    tmp.x += (tmp.width >> 1);
                    tmp.width = tmp.width >> 1;
                } else {
                    tmp = new Rectangle(r);
                    tmp.x += (tmp.width >> 1);
                    tmp.width = (tmp.width >> 1) + 1;
                }
                fillRect(g, tmp, r.x + r.width, r.y, start, r.x, r.y, end, clip);
                break;

            default:
            case FROM_CENTRE_GRADIENT_ON_Y:
                tmp = new Rectangle(r);
                tmp.height = tmp.height >> 1;
                fillRect(g, tmp, r.x, r.y, start, r.x, r.y + r.height, end, clip);

                if (r.height % 2 == 0) {
                    tmp = new Rectangle(r);
                    tmp.y += (tmp.height >> 1);
                    tmp.height = tmp.height >> 1;
                } else {
                    tmp = new Rectangle(r);
                    tmp.y += (tmp.height >> 1);
                    tmp.height = (tmp.height >> 1) + 1;
                }
                fillRect(g, tmp, r.x, r.y + r.height, start, r.x, r.y, end, clip);
                break;
        }
    }

    public static void fillRect(Graphics g, Rectangle r, float x1, float y1, Color color1, float x2, float y2, Color color2, Shape clip) {
        Shape oldClip = g.getClip();
        if (clip != null)
            g.setClip(clip);

        if (g instanceof Graphics2D) {
            Graphics2D g2D = (Graphics2D) g;

            Paint oldPaint = g2D.getPaint();

            if (g2D.getComposite() == null)
                g2D.setComposite(AlphaComposite.SrcAtop);

            g2D.setPaint(new GradientPaint(x1, y1, color1, x2, y2, color2));
            g2D.fillRect(r.x, r.y, r.width, r.height);
            g2D.setPaint(oldPaint);
        }
        if (clip != null)
            g.setClip(oldClip);
    }

    public static void fillRectTransparently(Graphics g, Rectangle r, Shape clip, int rule, float alpha) {
        Shape oldClip = g.getClip();
        if (clip != null)
            g.setClip(clip);

        if (g instanceof Graphics2D) {
            Graphics2D g2D = (Graphics2D) g;
            Composite oldComposite = g2D.getComposite();
            g2D.setComposite(AlphaComposite.getInstance(rule, alpha));

            g2D.fillRect(r.x, r.y, r.width, r.height);

            g2D.setComposite(oldComposite);
        }
        if (clip != null)
            g.setClip(oldClip);
    }


    public static void fillRect(Graphics g, Rectangle r, BufferedImage image) {
        if (g instanceof Graphics2D) {
            Graphics2D g2D = (Graphics2D) g;

            Paint oldPaint = g2D.getPaint();

            g2D.setPaint(new TexturePaint(image, r));
            g2D.fillRect(r.x, r.y, r.width, r.height);

            g2D.setPaint(oldPaint);
        }
    }


    public static String clippedText(String text, FontMetrics fm, int availTextWidth) {
        if (text == null || text.equals("")) {
            return "";
        }
        int textWidth = SwingUtilities.computeStringWidth(fm, text);
        String clipString = "...";
        if (textWidth > availTextWidth) {
            int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
            int nChars;
            for (nChars = 0; nChars < text.length(); nChars++) {
                totalWidth += fm.charWidth(text.charAt(nChars));
                if (totalWidth > availTextWidth) {
                    break;
                }
            }
            text = text.substring(0, nChars) + clipString;
        }
        return text;
    }

    public static int normalizeColor(int c) {
        return c > 255 ? 255 : c < 0 ? 0 : c;
    }

    public static Color getLinearInterpolationColor(Color color, int distance) {
        return new Color(normalizeColor(color.getRed() + distance),
                         normalizeColor(color.getGreen() + distance),
                         normalizeColor(color.getBlue() + distance));
    }

}
