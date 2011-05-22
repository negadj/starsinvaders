import processing.core.*; 
import processing.xml.*; 

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

public class spaceInvaders_camon09 extends PApplet {

/*
Versi\u00f3n del juego cl\u00e1ssico SPACE INVADERS programado para el taller
Processing y Arduino en CAMON de Alacant, que el autor impartido por 
Quelic Berga, Daniel Garc\u00eda i Joan Soler-Adillon
http://www.tucamon.es/contenido/processing-y-arduino

Autor: Joan Soler-Adillon [www.joan.cat]
Enero 2009
Licencia: Creative Commons, Attribution-Noncommercial-Share Alike 3.0 Unported
http://creativecommons.org/licenses/by-nc-sa/3.0/

Este programa est\u00e1 dise\u00f1ado para ser utilitzado tanto por usuarios noveles como avanzados. 
Para los primeros, existen una serie de variables al principio de todo (bajo el t\u00edtulo de 
"variables principales" que determinan todos los aspectos importantes de juego. S\u00f3lo manipulando
estos valores se ver\u00e1n cambios importantes en el juego. 
*/

//////////////////////
//  VARIABLES PRINCIPALES
//
//------PANTALLA------------:
//
//Ancho de la pantalla
int theWidth = 600;
//Alto de la pantalla
int theHeight = 400;
//
//------INVASORES------------:
//
//velocidad de los invasores
float invadersSpeed = 1;
//incremento de la velocidad cada vez que cambian de direcci\u00f3n
float invadersSpeedIncrement = 0.05f;
//pixels que bajan cada vez que cambian de direcci\u00f3n
int invadersYStep = 4;
//
//------NAVE------------:
//
//distancia de la nave al borde inferior de la pantalla
int spaceShipDistanceToBottom = 25;
//velocidad a la que se mueve la nave
int spaceShipSpeed = 5;
//
//------BALAS------------:
//
//Velocidad a la que van las balas
int bulletSpeed = 4;
//Tiempo (en milisegundos) que ha de pasar desde que se dispar\u00f3 una bala
//hasta que se puede disparar otra
int delayBetweenBullets = 500;
//
////// FIN DE VARIABLES PRINCIPALES
//////////////////////////////////////////////////
//creamos los objetos para el juego
int numOfInvaders = 50;
invader[] invaders = new invader[numOfInvaders];
spaceShip nave = new spaceShip(theWidth/2, theHeight-spaceShipDistanceToBottom,spaceShipSpeed, delayBetweenBullets);
//im\u00e1genes
PImage spaceShip, bulletImage,invadersFrameOne,invadersFrameTwo;
ArrayList bulletsList = new ArrayList();


public void setup(){
  size(theWidth,theHeight);
  imageMode(CENTER);
  //cargamos im\u00e1genes
  spaceShip = loadImage("nau.gif");
  invadersFrameOne = loadImage("bitxo1.gif");
  invadersFrameTwo = loadImage("bitxo2.gif");
  bulletImage = loadImage("bala.gif");
  spaceShipSpeed = 5;
  bulletSpeed = 4;
  //INICIALIZACION (esto funciona para 50 invasores a 10x5)
  int invaderCount = 0;
  for(int i=50;i<200;i+=30){
    for(int j=75;j<550;j+=50){
      invaders[invaderCount]=new invader(j,i,invaderCount, invadersSpeed, invadersSpeedIncrement, invadersYStep);
      invaderCount++;
    }
  }

}

public void draw(){
  background(0);
  for(int i=0;i<numOfInvaders;i++){
    invaders[i].update();
  }
  nave.update();
  if(bulletsList.size()>0){
    for(int i=0; i<bulletsList.size();i++){
      bullet _b = (bullet) bulletsList.get(i);
      _b.update(); 
    }
  }
}
///////////CONTROL CON TECLADO
//cuando le damos a una tecla
public void keyPressed(){
  //miramos si es de las raras:
  if (key == CODED) {
    //y si lo es, si es la flecha izquierda o derecha
    if (keyCode == LEFT) {
      nave.decrementX();
    } 
    else if (keyCode == RIGHT) { 
      nave.incrementX();
    } 
  } 
  else {
    //si le damos a la tecla espacio
    if(key==' '){
      nave.shoot();
    } 
  }
}




class bullet{
  float x,y,speed;
  bullet(float _x, float _s){
    x=_x;
    y=height-33;
    speed = _s;
  }
  public void update(){
    move();
    checkInvaders(); 
    checkUpperBorder();
    drawMe();
  }
  public void move(){
    y -= speed;
  }

  public void checkInvaders(){
    for(int i=0;i<numOfInvaders;i++){
      if(invaders[i].isAlive()){
        float ix = invaders[i].getX();
        float iy = invaders[i].getY();
        //teniendo en cuenta que los invaders son im\u00e1genes de 24x16
        if(abs(ix-x)<12 && abs(iy-y)<8){
          invaders[i].kill();
          removeMe();
        }
      }
    }
  }

  public void checkUpperBorder(){
    if(y<0){
      removeMe();
    }  
  }

  public void drawMe(){
    image(bulletImage,x,y);
  }

  public void removeMe(){
    bulletsList.remove(this); 
  }
}



class invader{
  float x,y,originX,originY;
  int dir=1;
  float speed, speedIncrement;
  int yStep;
  int distanceAllowed = 60;
  boolean alive = true ;
  int id = -1;
  int myTime = 0;
  int frameInterval = 200;
  boolean frameOne = true;

  invader(int _x, int _y, int _id, float _s, float _si, int _ys){
    x=originX=_x;
    y=originY=_y;
    id = _id;
    speed = _s;
    speedIncrement = _si;
    yStep = _ys;
  } 

  public void update(){
    if(alive){
      move();
      drawMe(); 
    }
  }

  public void move(){
    x += speed*dir;
    //if(id==20){
    //  println(x+"__"+originX);
    //  println((abs(originX-x)>distanceAllowed));
    //println("x="+x+"  s+d:"+speed*dir);  
    //}

    //Si llegamos al punto que hay que cambiar de direcci\u00f3n... 
    if(abs(x-originX)>distanceAllowed){
      dir = -dir;
      y += yStep; 
      speed += speedIncrement;
    }
  }

  public void drawMe(){
    if(millis() - myTime > frameInterval){
      myTime = millis();
      frameOne = !frameOne;
    }
    if(frameOne){
      image(invadersFrameOne,x,y);
    } 
    else {
      image(invadersFrameTwo,x,y);
    }
  }

  //GETs y SETs
  public void setX(int _x){
    x=_x;
  }
  public void setY(int _y){
    y=_y;
  }
  public float getX(){
    return x;
  }
  public float getY(){
    return y;
  }
  public void kill(){
    alive = false; 
  }
  public boolean isAlive(){
    if(alive)return true;
    else return false;
  }

}





class spaceShip{
  int x, y;
  int xStep, delayBetweenBullets;
  int lastBulletTime = -10000;
  spaceShip(int _x, int _y, int _s, int _d){
    x = _x;
    y = _y;
    xStep = _s;
    delayBetweenBullets = _d;
  } 

  public void update(){
    drawMe(); 
    checkInvaders();
  }

  public void drawMe(){
    //println("_____________"+x+"oo"+y);
    image(spaceShip,x,y);
  }

  public void incrementX(){
    x += xStep;
  }
  public void decrementX(){
    x -= xStep;
  }
  public void setX(int _newX){
    x = _newX; 
  }

  public void shoot(){
    if(millis()-lastBulletTime > delayBetweenBullets){ 
      bulletsList.add(new bullet(x,bulletSpeed));
      lastBulletTime = millis();
    }
  } 

  public void checkInvaders(){
    for(int i=0;i<numOfInvaders;i++){
      if(invaders[i].isAlive()){
        float iy = invaders[i].getY();
        if(iy >= height-spaceShipDistanceToBottom-15){
          //SE ACAB\u00d3 EL JUEGO
          fill(255,0,0);
          stroke(255,0,0);
          rect(0,0,width,height);
        }
      }
    }
  }
}


  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#F0F0F0", "spaceInvaders_camon09" });
  }
}
