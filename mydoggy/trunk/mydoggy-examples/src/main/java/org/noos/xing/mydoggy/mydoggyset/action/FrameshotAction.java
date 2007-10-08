package org.noos.xing.mydoggy.mydoggyset.action;

import com.flagstone.transform.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FrameshotAction extends AbstractAction {
    protected JFrame frame;
    protected File dir;
    protected File dirThumb;
    protected int index = 0;

    public FrameshotAction(JFrame frame) {
        super("Frameshot");
        this.frame = frame;
        this.dir = new File("shots");
        dir.mkdirs();
        this.dirThumb = new File(dir, "thumb");
        dirThumb.mkdirs();
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Robot robot = new Robot();
                    BufferedImage image = robot.createScreenCapture(frame.getBounds());
                    ImageIO.write(image, "png", new File(dir, "screenshot_" + index + ".png"));

                    Image scaledImage = image.getScaledInstance(150, 113, Image.SCALE_SMOOTH);

                    BufferedImage scaled = new BufferedImage(150, 113, BufferedImage.TYPE_INT_RGB);
                    scaled.getGraphics().drawImage(scaledImage, 0, 0, null);
                    
                    ImageIO.write(scaled, "png", new File(dirThumb, "screenshot_" + index + ".png"));
                    index++;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
// Create an initially empty movie structure that holds the movie’s
   // header information and tags.

   FSMovie movie = new FSMovie ();

   // Establish the movie’s bounds such that the upper-left corner locates
   // at (0, 0) and the lower-right corner locates at (400, 400).

   movie.setFrameSize (new FSBounds (0, 0, 400, 400));

   // Establish the movie’s frame rate as 24 frames per second.

   movie.setFrameRate (24.0f);

   // Add a SetBackgroundColor tag to the movie. This tag identifies green
   // as the background color.

   movie.add (new FSSetBackgroundColor (new FSColor (0, 255, 0)));

   // Specify the boundaries for a square shape.

   FSBounds bounds = new FSBounds (0, 0, 50, 50);

   // Define the fill- and line-styles for the shape.

   ArrayList<FSFillStyle> fillStyles = new ArrayList<FSFillStyle> ();
   fillStyles.add (new FSSolidFill (new FSColor (255, 0, 0)));

   ArrayList<FSLineStyle> lineStyles = new ArrayList<FSLineStyle> ();
   lineStyles.add (new FSSolidLine (1, new FSColor (0, 0, 0)));

   // Construct the square shape.

   FSShape shape = new FSShape ();

   // FSShapeStyle specifies the 1-based indices into the fill- and line-
   // styles arrays that are passed to FSDefineShape. The final two zeros
   // identify the origin where drawing begins. This origin is the square
   // shape’s upper-left corner.

   shape.add (new FSShapeStyle (1, 1, 0, 0, 0));
   shape.add (new FSLine (50, 0));
   shape.add (new FSLine (0, 50));
   shape.add (new FSLine (-50, 0));
   shape.add (new FSLine (0, -50));

   // Define the square shape in terms of its bounds, fill-style, line-style,
   // and shape.

   FSDefineShape shape2 = new FSDefineShape (movie.newIdentifier (), bounds,
                        fillStyles, lineStyles, shape);

   // Add the DefineShape tag to the movie.

   movie.add (shape2);

   // Animate the square over the background by moving the square in a
   // circle. The animation begins toward the bottom and in the middle of
   // the movie player’s window.

   for (double angle = 0.0; angle <= 3600.0; angle += 1.0)
   {
      double rads = Math.toRadians (angle);

      // Add a PlaceObject tag to the movie. This tag places the square
      // object on layer #1 of the movie player’s display list.

      movie.add (new FSPlaceObject(shape2.getIdentifier (), 1,
                     (int) (175 + Math.sin (rads)*150),
                     (int) (175 + Math.cos (rads)*150)));

      // Add a ShowFrame tag to the movie. This tag causes the entire
      // display list to be viewed in the movie player’s window.

      movie.add (new FSShowFrame ());

      // Add a RemoveObject tag to the movie. This tag removes the square
      // object from layer #1 in the display list. If this tag was not
      // present, the square would not move around the movie player’s
      // window. Once an object has been placed on the display list on a
      // given layer, it must be removed and re-added to place the object
      // in a new position on that layer.

      movie.add (new FSRemoveObject (shape2.getIdentifier (), 1));
   }

   // Attempt to encode, possibly compress (Flash 6+), and output the movie
   // to a SWF file named squarecircle.swf.

   try
   {
     movie.encodeToFile ("squarecircle.swf");
   }
   catch (Exception e)
   {
     System.out.println (e);
   }
  }

}
