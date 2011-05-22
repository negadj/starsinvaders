import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import remixlab.proscene.Scene;

public class Invaders extends Grid {

	public boolean drawGrid = true;
	public PImage invadersFrameOne, invadersFrameTwo, naveImage;

	int numOfInvaders = 50;
	invader[] invaders = new invader[numOfInvaders];

	// ------INVASORES------------:
	//
	// velocidad de los invasores
	float invadersSpeed = 1;
	// incremento de la velocidad cada vez que cambian de direcci\u00f3n
	float invadersSpeedIncrement = 0.05f;
	// pixels que bajan cada vez que cambian de direcci\u00f3n
	int invadersYStep = 4;
	// ------BALAS------------:
	//
	// Velocidad a la que van las balas
	int bulletSpeed = 4;
	// Tiempo (en milisegundos) que ha de pasar desde que se dispar\u00f3 una
	// bala
	// hasta que se puede disparar otra
	int delayBetweenBullets = 500;
	ArrayList bulletsList = new ArrayList();
	//
	// ------NAVE------------:
	//
	// distancia de la nave al borde inferior de la pantalla
	int spaceShipDistanceToBottom = 25;
	// velocidad a la que se mueve la nave
	int spaceShipSpeed = 5;
	SpaceShip nave;

	public Invaders(PApplet applet, Scene scene) {
		super(applet, scene);

		invadersFrameOne = applet.loadImage("bitxo1.gif");
		invadersFrameTwo = applet.loadImage("bitxo2.gif");
		naveImage = applet.loadImage("nau.gif");

		applet.imageMode(PApplet.CENTER);
		// cargamos im\u00e1genes

		spaceShipSpeed = 5;
		bulletSpeed = 4;
		// INICIALIZACION (esto funciona para 50 invasores a 10x5)
		int invaderCount = 0;
		for (int i = 50; i < 200; i += 30) {
			for (int j = 75; j < 550; j += 50) {
				invaders[invaderCount] = new invader(applet, j, i,
						invaderCount, invadersSpeed, invadersSpeedIncrement,
						invadersYStep);
				invaders[invaderCount].invadersFrameOne = invadersFrameOne;
				invaders[invaderCount].invadersFrameTwo = invadersFrameTwo;
				invaderCount++;
			}
		}

		nave = new SpaceShip(applet, 500, 400, spaceShipSpeed, delayBetweenBullets);
		nave.spaceShip = naveImage;
	}

	@Override
	public void draw() {
		if (drawGrid)
			super.draw();

		applet.pushMatrix();
		applet.pushStyle();
		// Multiply matrix to get in the frame coordinate system.
		// scene.parent.applyMatrix(iFrame.matrix()) is handy but inefficient
		iFrame.applyTransformation(); // optimum
		// applet.noStroke();

		applet.translate(-300, -350, 0);

		if (iFrame.grabsMouse())
			applet.stroke(255, 0, 0);
		else
			applet.stroke(getColor());

		for (int i = 0; i < numOfInvaders; i++) {
			// invaders[i].update();
			invaders[i].draw();
		}

		if (bulletsList.size() > 0) {
			for (int i = 0; i < bulletsList.size(); i++) {
				Bullet b = (Bullet) bulletsList.get(i);
				b.update();
				b.drawMe();
				
			}
		}

		nave.update();
		nave.drawMe();

		applet.popStyle();
		applet.popMatrix();

	}

	public void shoot(PApplet pApplet) {
		bulletsList.add(new Bullet(pApplet, nave.x, nave.y, bulletSpeed));
	}

}
