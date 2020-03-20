/*
 * Author: Brock A. Allton
 * Date: 26 August 2019
 * Purpose: GUI set up to display/perfrom transformation on the images that are
 * created in the Java2DCreator class. Utilized the CMSC405P1Template as a guide
 */
package java2ddrawer;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

public class Java2DDrawer extends JPanel{
    
    //Counter that will increase by one in each frame.
    private int frameNumber;
    //time in milliseconds since the animcation started.
    private long elapsedTimeMillis;
    //This is the measure of a pixel in the coordinate system
    //set up by calling the applyLimits method.  It can be used
    //for setting line widths, for example.
    private float pixelSize;
     
    //Variables setup for the transformations to be performed
    static int translateX = 0;
    static int translateY = 0;
    static double rotation = 0.0;
    static double scaleX = 1.0;
    static double scaleY = 1.0;
    
    Java2DCreator images = new Java2DCreator();
    BufferedImage fImage = images.getImage(Java2DCreator.letterF);
    BufferedImage uImage = images.getImage(Java2DCreator.letterU);
    BufferedImage stripeImage = images.getImage(Java2DCreator.christmasStripes);
    

    public static void main(String[] args) {
        //Set up the window with the title
        JFrame window = new JFrame("Java 2D Graphics Animator");
        //Set up the drawing area
        final Java2DDrawer panel = new Java2DDrawer();
        //Shows the panel in the window
        window.setContentPane(panel);
        //Closes out the program upon exiting
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set the window to open in the middle of the screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation((screen.width - window.getWidth()) / 2,
        (screen.height - window.getHeight()) / 2);
        //Set so the window cannot be resized
        window.setResizable(false);
        //Set window size based on the preferred sizes of its contents
        window.pack();
        //Timer which will emit events to drive the animation
        Timer animationTimer;
        //variable to get the system time to keep track of the animation time
        final long startTime = System.currentTimeMillis();
        //Taken from AnimationStart
        //Modified to change the timing and allow for recycling
        animationTimer = new Timer(1100, new ActionListener(){
            public void actionPerformed(ActionEvent arg0){
                if(panel.frameNumber > 5){
                    panel.frameNumber = 0;
                }else{
                    panel.frameNumber++;
                }
                panel.elapsedTimeMillis = System.currentTimeMillis() - startTime;
                panel.repaint();
            }
        });
        //Set so the window is visible on the screen when the program runs, can't
        //see anything without it
        window.setVisible(true);
        //Gets the animation rolling
        animationTimer.start();   
    }//End public static void main
    
    public Java2DDrawer(){
        //Size of the frame
        setPreferredSize(new Dimension(800,600));
    }//End public Java2DDrawer()
    
    //This is where the magic happens!
    //Code taken from AnimationStarter.java but modified to add the specific images
    //Also, added looping structure for different animations
    protected void paintComponent(Graphics g){
        
        //First, create a Graphics2D drawing context for drawing on the panel.
        //(g.create() makes a copy of g, which will draw to the same place as g,
        //but changes to the returned copy will not affect the original.)
        Graphics2D g2 = (Graphics2D) g.create();
        
        //Turn on antialiasing in this graphics context, for better drawing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Fill in the drawing area with white
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());//From the old graphics API!
        
        //Here, I set up a new coordinate system on the drawing area, by calling
        //the applyLimits() method that is defined below.  Without this call, I
        //would be using regular pixel coordinates.  This function sets the value
        //of the global variable pixelSize, which I need for stroke widths in the
        //transformed coordinate system.
        // Controls your zoom and area you are looking at
        applyWindowToViewportTransformation(g2,-75, 75, -75, 75, true);
        AffineTransform savedTransform = g2.getTransform();
        double frameTime = elapsedTimeMillis / 1000.0;
        System.out.println("Frame: " + frameNumber);
        System.out.println("Time taken: " + frameTime + " Seconds");
        switch(frameNumber){
            //The first frame will be unmodified
            case 1:
                translateX = 0;
                translateY = 0;
                scaleX = 1.0;
                scaleY = 1.0;
                rotation = 0;
                break;
            //Second frame rotates 90 degrees clockwise, didn't like it 
            //going through the motions on their sides, so wanted them upright
            case 2:
                rotation = -(90*Math.PI / 180);
                break;
            //Third frame translates images by (-5,7)
            case 3:
                translateX = -5;
                translateY = 7;
                break;
            //Fourth frame rotates images by 45 degrees counterclockwise
            case 4:
                rotation = 45*Math.PI / 180;
                break;
            //Fifth frame rotates images by 90 degrees clockwise
            case 5:
                rotation = -(90*Math.PI / 180);
                break;
            //Sixth frame scales 2x, .5y
            case 6:
                scaleX = 2.0;
                scaleY = 0.5;
                break;
            default:
                break;
        }//End switch(frameNumber)
        
        //Add the letter F image
        //Move image
        g2.translate(translateX, translateY);
        //To offset translates again
        g2.translate(20, -40);
        g2.rotate(rotation);//Rotates the image
        g2.scale(scaleX, scaleY);//Scales the image
        g2.drawImage(fImage, 0, 0, this);//Draws the image
        g2.setTransform(savedTransform);
        
        //Add the letter U imaage
        //Uses the same set up as above for the letter F
        g2.translate(translateX, translateY);
        g2.translate(-20, 30);
        g2.rotate(rotation);
        g2.scale(scaleX,scaleY);
        g2.drawImage(uImage, 0, 0, this);
        g2.setTransform(savedTransform);
        
        //Add the Christmas Stripes image
        //Uses the same set up as the above two
        g2.translate(translateX, translateY);
        g2.translate(40,30);
        g2.rotate(rotation);
        g2.scale(scaleX, scaleY);
        g2.drawImage(stripeImage, 0, 0, this);
        g2.setTransform(savedTransform);   
    }//End protected void paintComponent(Graphics g)
    
    //Method taken directly from AnimationStarter.java Code
    private void applyWindowToViewportTransformation(Graphics2D g2, double left,
            double right, double bottom, double top, boolean preserveAspect){
        
        //Width and height of the drawing area in pixels
        int width = getWidth();
        int height = getHeight();
        if(preserveAspect){
            //Adjust the limits to match the aspect ratio of the drawing area
            double displayAspect = Math.abs((double)height/width);
            double requestedAspect = Math.abs((bottom - top) / (right - left));
            if(displayAspect > requestedAspect){
                //Expand viewport vertically
                double excess = (bottom - top) * 
                        (displayAspect / requestedAspect -1);
                bottom += excess / 2;
                top += excess / 2;
            }else if(displayAspect < requestedAspect){
                double excess = (right - left) * 
                        (requestedAspect / displayAspect - 1);
                right += excess / 2;
                left += excess / 2;
            }//End else if(displayAspect < requestedAspect)
        }//End if(preserveAspect)
        g2.scale(width / (right - left), height / (bottom - top));
        g2.translate(-left, -top);
        double pixelWidth = Math.abs((right - left) / width);
        double pixelHeight = Math.abs((bottom - top) / height);
        pixelSize = (float) Math.max(pixelWidth, pixelHeight);
    }//End private void applyWindowToViewportTransformation 
}//end public class Java2DDrawer
