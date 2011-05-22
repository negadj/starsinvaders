import processing.core.*; 
import processing.xml.*; 

import processing.opengl.*; 
import remixlab.proscene.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class CajasOrientadas extends PApplet {



/**
 * Cajas Orientadas.
 * by Jean Pierre Charalambos.
 * 
 * This example illustrates some basic Frame properties, particularly how to orient them.
 * Select and move the sphere (holding the right mouse button pressed) to see how the
 * boxes will immediately be oriented towards it. You can also pick and move the boxes
 * and still they will be oriented towards the sphere.
 *
 * Press 'h' to display the global shortcuts in the console.
 * Press 'H' to display the current camera profile keyboard shortcuts
 * and mouse bindings in the console.
 */



Scene scene;
Box [] cajas;
Sphere esfera;
int radius = 100;

public void setup() {
  size(640, 360, OPENGL);
  scene = new Scene(this);
  // press 'f' to display frame selection hints
  scene.setShortcut('f', Scene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);
  scene.setAxisIsDrawn(false);
  scene.setRadius(1060);
  scene.showAll();
  
  esfera = new Sphere();
  esfera.setPosition(new PVector(0, 140, 0));
  esfera.setColor(color(0,0,255));
  
  // create an array of boxes with random positions, sizes and colors 
  cajas = new Box[30];
  for (int i = 0; i < cajas.length; i++)
    cajas[i] = new Box();
}

public void draw() {
  //Proscene sets the background to black by default. If you need to change
  //it, don't call background() directly but use scene.background() instead.
  esfera.draw();
  for (int i = 0; i < cajas.length; i++) {
    // orient the boxes according to the sphere position
    cajas[i].setOrientation(esfera.getPosition());
    cajas[i].draw(true);
  }
}

public void keyPressed() {
		switch (key) {
		case 'o':
			radius+=10;
			scene.setRadius(radius);
			println(radius);
			break;
		case 'l':
			radius-=10;
			scene.setRadius(radius);
			println(radius);
			break;
		default:

			break;
		}
}
/**
 * Box. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the Mouse Grabber example.
 *
 * Any object that needs to be "pickable" (such as the Box), should be
 * attached to its own InteractiveFrame. That's all there is to it.
 *
 * The built-in picking proscene mechanism actually works as follows. At
 * instantiation time all InteractiveFrame objects are added to a mouse
 * grabber pool. Scene parses this pool every frame to check if the mouse
 * grabs a InteractiveFrame by projecting its origin onto the screen.
 * If the mouse position is close enough to that projection (default
 * implementation defines a 10x10 pixel square centered at it), the object
 * will be picked. 
 *
 * Override InteractiveFrame.checkIfGrabsMouse if you need a more
 * sophisticated picking mechanism.
 *
 * Observe that this class is used among many examples, such as MouseGrabber
 * CajasOrientadas, PointUnderPixel and ScreenDrawing. Hence, it's quite
 * complete, but its functionality is not totally exploited by this example.
 */

public class Box {
  InteractiveFrame iFrame;
  float w, h, d;
  int c;

  Box() {
    iFrame = new InteractiveFrame(scene);
    setSize();
    setColor();
    setPosition();
  }
  
  // don't draw local axis
  public void draw() {
    draw(false);
  }

  public void draw(boolean drawAxis) {
    pushMatrix();
    pushStyle();
    // Multiply matrix to get in the frame coordinate system.
    // scene.parent.applyMatrix(iFrame.matrix()) is handy but inefficient
    iFrame.applyTransformation(); //optimum
    if(drawAxis) scene.drawAxis(max(w,h,d)*1.3f);
    noStroke();
    if (iFrame.grabsMouse())
      fill(255, 0, 0);
    else
      fill(getColor());
    //Draw a box
    box(w,h,d);
    popStyle();
    popMatrix();
  }
  
  // sets size randomly
  public void setSize() {
    w = random(10, 40);
    h = random(10, 40);
    d = random(10, 40);
  }
  
  public void setSize(float myW, float myH, float myD) {
    w=myW; h=myH; d=myD;
  }
  
  public int getColor() {
    return c;
  }
  
  // sets color randomly
  public void setColor() {
    c = color(random(0, 255), random(0, 255), random(0, 255));
  }
  
  public void setColor(int myC) {
    c = myC;
  }
  
  public PVector getPosition() {
    return iFrame.position();
  }
  
  // sets position randomly
  public void setPosition() {
    float low = -100;
    float high = 100;
    iFrame.setPosition(new PVector(random(low, high), random(low, high), random(low, high)));
  }
  
  public void setPosition(PVector pos) {
    iFrame.setPosition(pos);
  }
  
  public Quaternion getOrientation() {
    return iFrame.orientation();
  }
  
  public void setOrientation(PVector v) {
    PVector to = PVector.sub(v, iFrame.position());
    iFrame.setOrientation(new Quaternion(new PVector(0,1,0), to));
  }
}
/**
 * Esfera. 
 * by Jean Pierre Charalambos.
 * 
 * This class is part of the Cajas Orientadas example.
 *
 * Any object that needs to be "pickable" (such as the Esfera), should be
 * attached to its own InteractiveFrame. That's all there is to it.
 *
 * The built-in picking proscene mechanism actually works as follows. At
 * instantiation time all InteractiveFrame objects are added to a mouse
 * grabber pool. Scene parses this pool every frame to check if the mouse
 * grabs a InteractiveFrame by projecting its origin onto the screen.
 * If the mouse position is close enough to that projection (default
 * implementation defines a 10x10 pixel square centered at it), the object
 * will be picked.
 *
 * Override InteractiveFrame.checkIfGrabsMouse if you need a more
 * sophisticated picking mechanism.
 */

public class Sphere {
  InteractiveFrame iFrame;
  float r;
  int c;

  Sphere() {
    iFrame = new InteractiveFrame(scene);
    setRadius(10);
  }

  public void draw() {
    draw(true);
  }
  
  public void draw(boolean drawAxis) {    
    pushMatrix();
    pushStyle();
    noStroke();
    // Multiply matrix to get in the frame coordinate system.
    // scene.parent.applyMatrix(iFrame.matrix()) is handy but inefficient
    iFrame.applyTransformation(); //optimum
    if(drawAxis) scene.drawAxis(radius()*1.3f);
    if (iFrame.grabsMouse()) {
      fill(255, 0, 0);
      sphere(radius()*1.2f);
    }
    else {
      fill(getColor());
      sphere(radius());
    }
    popStyle();
    popMatrix();
  }

  public void setPosition(PVector pos) {
    iFrame.setPosition(pos);
  }

  // We need to retrieve the Esfera's position for the Cajas to orient towards it. 
  public PVector getPosition() {
    return iFrame.position();
  }
  
  public float radius() {
    return r;
  }
  
  public void setRadius(float myR) {
    r = myR;
  }
  
  public int getColor() {
    return c;
  }
  
  public void setColor() {
    c = color(random(0, 255), random(0, 255), random(0, 255));
  }
  
  public void setColor(int myC) {
    c = myC;
  }
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#F0F0F0", "CajasOrientadas" });
  }
}
