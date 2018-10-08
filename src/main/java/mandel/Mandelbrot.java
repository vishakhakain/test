
package mandel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
 /**
 *
 * @author Visakha
 */   
  public class Mandelbrot extends JApplet{

   display canvas;       // The drawing surface on which the Mandelbrot set is displayed.
                  
               
   JButton stopButton, startButton;  // Computation will start when
                                     // Start button is pressed and will
                                     // continue until it finishes or the
                                     // user presses the "Stop" button.

  
   public void init() {             // Initialize the applet by creating the canvas
                                    // and buttons and adding them to the applet's
                                    // content pane.
          
      setBackground(Color.gray);
      System.out.println("init");
      
      canvas = new display();
      getContentPane().add(canvas, BorderLayout.CENTER);
      
      JPanel bottom = new JPanel();
      bottom.setBackground(Color.gray);
      startButton = new JButton("Start");
      startButton.addActionListener(canvas);
      bottom.add(startButton);
      stopButton = new JButton("Stop");
      stopButton.addActionListener(canvas);
      bottom.add(stopButton);
      stopButton.setEnabled(false);      
      getContentPane().add(bottom, BorderLayout.SOUTH);
      
   }                                // end init();
   
   
   public Insets getInsets() {      // Leave space around the applet that will show in the

       System.out.println("insets");                            // background color, gray.
        return new Insets(4,4,4,4);     
   }
   
   public void stop() {             // This method is called by the system when the applet
                                    // is about to be temporarily or permanently stopped.
                                    // To the canvas to stop the computation thread, if
                                    // it is running.       
       System.out.println("stop");
       canvas.stopRunning();
   }

     public class display extends JPanel implements ActionListener, Runnable {
        

       Image OSI;    // An off-screen images that holds the picture
                    //    of the Mandelbrot set.  This is copied onto
                    //    the drawing surface, if it exists.  It is
                    //    created by the computational thread.

      Graphics OSG; // A graphics context for drawing on OSI.

      Thread runner;    
      boolean running;  // set to true when the thread is running.
      
      double xmin = -2.5;   // The ranges of x and y coordinates that
      double xmax = 1;      //    are represented by this drawing surface
      double ymin = -1.25;
      double ymax = 1.25;
   
      public void paintComponent(Graphics g) {  //paint the drawing surface
            if (OSI == null) {
            g.setColor(Color.black);
            g.fillRect(1,2,getWidth(),getHeight());
         }
         else {
            g.drawImage(OSI,0,0,null);
         }
      }
      
      public void actionPerformed(ActionEvent evt) {    //action perform according to user selecting start or stop button
          System.out.println("actionPerformed");
         String command = evt.getActionCommand();
         if (command.equals("Start"))
            startRunning();
         else if (command.equals("Stop"))
            stopRunning();
      }
      
      void startRunning() {    //starts the computational thread
           if (running)
            return;
         runner = new Thread(this);     // Creates a thread that will execute the run()
              
         running = true;
         runner.start();
      }
      
      void stopRunning() {  //stops the computational thread
           running = false;
      }
      
      int countIterations(double x, double y) { // The Mandelbrot set is represented by coloring
                                                // each point (x,y) according to the number of
                                                // iterations it takes before the while loop in 
                                                // this method ends.  For points that are actually
                                                // in the Mandelbrot set, or very close to it, the 
                                                // count will reach the maximum value, 80.  These
                                                // points will be colored purple.  All other colors
                                                // represent points that are definitely NOT in the set.

            
         int count = 0;
         double zx = x;
         double zy = y;
         while (count < 80 && Math.abs(x) < 100 && Math.abs(zy) < 100) {
            double new_zx = zx*zx - zy*zy + x;
            zy = 2*zx*zy + y;
            zx = new_zx;
            
            count++;
         }
         return count;
      }
      
      int i,j,size,colorIndex;   

         
      Runnable painter = new Runnable() {
               
            public void run() {
               int left = i - size/2;
               int top = j - size/2;
               OSG.setColor( Color.getHSBColor(colorIndex/100.0F,1F,1F) );
               OSG.fillRect(left,top,size,size);
               paintImmediately(left,top,size,size);
            }
        };
      

      public void run() {   // It draws the Mandelbrot set in a series of passes of increasing resolution.
          System.out.println("run");
         startButton.setEnabled(false);  
         stopButton.setEnabled(true);    
         int width = getWidth();   // Current size of this canvas.
         int height = getHeight();

         OSI = createImage(getWidth(),getHeight()); // Create the off-screen image
         OSG = OSI.getGraphics();
         OSG.setColor(Color.black);
         OSG.fillRect(0,0,width,height);
         
         for (size = 64; size >= 1 && running; size = size/2) {
               
            double dx,dy;  
            dx = (xmax - xmin)/width * size;
            dy = (ymax - ymin)/height * size;
            double x = xmin + dx/2;  
            for (i = size/2; i < width+size/2 && running; i += size) {
                  
               double y = ymax - dy/2;
               
               for (j = size/2; j < height+size/2 && running; j += size) {
                      colorIndex = countIterations(x,y);
                   try {
                      SwingUtilities.invokeAndWait(painter);
                   }
                   catch (Exception e) {
                   }
                   y -= dy;
               }
               x += dx;
               Thread.yield();  // Give other threads a chance to run.
            }
         }

         running = false;  

         startButton.setEnabled(true);  // Reset states of buttons.
         stopButton.setEnabled(false);
      } 
   }

     public static void main(String args[]) {
         System.out.println("main");
         Mandelbrot fractal=new Mandelbrot();
         JFrame myFrame=new JFrame("Test");
         myFrame.add(fractal);
         myFrame.pack();
         myFrame.setVisible(true);
         myFrame.setSize(820, 520);
         fractal.init();
         fractal.start();
     }


 }
