import processing.core.*; 
import processing.xml.*; 

import processing.opengl.*; 
import wblut.hemesh.*; 
import wblut.hemesh.modifiers.*; 
import wblut.hemesh.creators.*; 
import wblut.geom.*; 

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

public class voronoiHemesh2 extends PApplet {



//requires hemesh 1.2.3





float[][] points;
int numpoints;
HE_Mesh container;
HE_Mesh[] cells;
int numcells;
WB_Plane P1,P2;

public void setup() {
  size(800,800,OPENGL);
  //create a sphere
  HEC_Geodesic geo=new HEC_Geodesic(this);
  geo.setRadius(300).setLevel(2); 
  container=new HE_Mesh(geo);
  
  //slice off most of both hemispheres
  P1=new WB_Plane(new WB_Point(0,0,-10), new WB_Vector(0,0,1));
  P2=new WB_Plane(new WB_Point(0,0,10), new WB_Vector(0,0,-1));
  HEM_Slice s=new HEM_Slice().setPlane(P1);
  container.modify(s);
  s=new HEM_Slice().setPlane(P2);
  container.modify(s);
  
  //generate points
  numpoints=50;
  points=new float[numpoints][3];
  for(int i=0;i<numpoints;i++) {
    points[i][0]=random(-250,250);
    points[i][1]=random(-250,250);
    points[i][2]=random(-20,20);
  }
  
  //generate voronoi cells
  HEMC_VoronoiCells vcmc=new HEMC_VoronoiCells(this);
  vcmc.setPoints(points).setContainer(container).setOffset(5);
  cells=vcmc.create();
  numcells=cells.length;
}

public void draw() {
  background(255);
  lights();
  translate(width/2,height/2,0);
  rotateX(1f/height*mouseY*TWO_PI-PI);
  rotateY(1f/width*mouseX*TWO_PI-PI);
  drawFaces();
  drawEdges();
}

public void drawEdges(){
  smooth();
  stroke(0);
  strokeWeight(2);
  for(int i=0;i<numcells;i++) {
    cells[i].drawEdges();
  } 
}

public void drawFaces(){
  noSmooth();
  noStroke();
  for(int i=0;i<numcells;i++) {
    fill(100+i,i,i);
    cells[i].drawFaces();
  }   
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#F0F0F0", "voronoiHemesh2" });
  }
}
