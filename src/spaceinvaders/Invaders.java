package spaceinvaders;

import java.util.ArrayList;

import javax.media.opengl.GL;

import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLGraphicsOffScreen;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import remixlab.proscene.Scene;
import spaceinvaders.com.OscFacade;

public class Invaders extends Grid {

	public boolean drawGrid = true;
	public PImage invadersFrameOne, invadersFrameTwo, naveImage;

	int numOfInvadersX = 5;
	int numOfInvadersY = 5;
	Invader[] invaders = new Invader[numOfInvadersX * numOfInvadersY];

	// ------INVASORES------------:
	//
	// velocidad de los invasores
	float invadersSpeed = 1;
	// incremento de la velocidad cada vez que cambian de direcci\u00f3n
	float invadersSpeedIncrement = 0.05f;
	// pixels que bajan cada vez que cambian de direcci\u00f3n
	int invadersYStep = 4;

	//
	// ------NAVE------------:
	//
	// distancia de la nave al borde inferior de la pantalla
	int spaceShipDistanceToBottom = 25;
	// velocidad a la que se mueve la nave
	int spaceShipSpeed = 5;
	SpaceShip nave;

	// ------BALAS------------:
	//
	// Velocidad a la que van las balas
	int bulletSpeed = 4;
	ArrayList<Bullet> bulletsList = new ArrayList<Bullet>();
	ArrayList<Bullet> bulletsListInvaders = new ArrayList<Bullet>();

	OscFacade oscFacade;

	public Invaders(PApplet applet, PGraphics graphics, Scene scene,
			int unitSize) {
		super(applet, graphics, scene, unitSize);

		invadersFrameOne = applet.loadImage("bitxo1.gif");
		invadersFrameOne.resize(invadersFrameOne.width * unitSize,
				invadersFrameOne.height * unitSize);
		invadersFrameTwo = applet.loadImage("bitxo2.gif");
		invadersFrameTwo.resize(invadersFrameTwo.width * unitSize,
				invadersFrameTwo.height * unitSize);

		naveImage = applet.loadImage("nau.gif");
		naveImage.resize(naveImage.width * unitSize, naveImage.height
				* unitSize);

		graphics.imageMode(PApplet.CENTER);
		// cargamos im\u00e1genes

		spaceShipSpeed = 5;
		// INICIALIZACION (esto funciona para 50 invasores a 10x5)
		int invaderCount = 0;
		for (int i = 0; i < invaders.length; i += 1) {
			invaders[invaderCount] = new Invader(applet, graphics, 0, 0,
					invaderCount, invadersSpeed, invadersSpeedIncrement,
					invadersYStep, unitSize);
			invaderCount++;
		}

		nave = new SpaceShip(applet, graphics, 500, 400, spaceShipSpeed, 500);
		nave.spaceShip = naveImage;

		oscFacade = new OscFacade();
		oscFacade.setup(applet, "127.0.0.1", 12000);
	}

	@Override
	public void draw() {

		if (drawGrid)
			super.draw();

		graphics.pushMatrix();
		graphics.pushStyle();
		// Multiply matrix to get in the frame coordinate system.
		// scene.parent.applyMatrix(iFrame.matrix()) is handy but inefficient
		iFrame.applyTransformation(); // optimum
		// graphics.noStroke();

		graphics.translate(-300, -350, 0);

		if (iFrame.grabsMouse())
			graphics.stroke(255, 0, 0);
		else
			graphics.stroke(getColor());

		GLGraphicsOffScreen renderer = (GLGraphicsOffScreen) graphics;
		renderer.beginGL();

		// We get the gl object contained in the GLGraphics renderer.
		GL gl = renderer.gl;

		// Now we can do direct calls to OpenGL:
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);

		// Disabling depth masking to properly render a semitransparent
		// object without using depth sorting.
		gl.glDepthMask(false);

		for (int i = 0; i < numOfInvadersX; i++) {
			for (int j = 0; j < numOfInvadersY; j++) {
				// invaders[i].update();
				renderer.pushMatrix();
				renderer.translate(12 * 35 * i, j * 50, 12 * 30 * j);
				renderer.rotateZ(applet.radians(90));
				renderer.rotateY(applet.radians(150));
				renderer.model(invaders[0].xcubes);
				renderer.popMatrix();
			}
		}
		

		// DISPAROS DE LOS INVASORES
		if (applet.frameCount % 50 == 0) {
			int selected = (int) applet.random(invaders.length);
			Bullet bullet = invaders[selected].shoot();
			invaderShoot(bullet);
			if (bullet != null)
				bulletsListInvaders.add(bullet);
		}

		// MANEJO DE LOS DISPAROS DE LA NAVE
		// TODO optimizar y eliminar del array los muertos
		if (bulletsList.size() > 0) {
			for (int i = 0; i < bulletsList.size(); i++) {
				Bullet b = (Bullet) bulletsList.get(i);
				b.checkCollision(invaders);
				b.update();
				b.drawMe();
			}
		}
		// MANEJO DE LOS DISPAROS DE LOS INVASORES
		// TODO optimizar y eliminar del array los muertos
		if (bulletsListInvaders.size() > 0) {
			for (int i = 0; i < bulletsListInvaders.size(); i++) {
				Bullet b = (Bullet) bulletsListInvaders.get(i);
				boolean collision = nave.checkCollision(b.x, b.y);
				if (collision) {
					System.out.println("augh! me han dado");
				}
				b.update();
				b.drawMe();
			}
		}

		nave.update();
		nave.drawMe();

		if (startShoot > 0) {
			applet.pushStyle();
			applet.strokeWeight(startShoot);
			applet.line(nave.x, 0, 0, nave.x, 1000, 1000);
			applet.popStyle();
		}
		
		gl.glDepthMask(true);

		renderer.endGL();
		
		graphics.popStyle();
		graphics.popMatrix();
	}

	public long startShoot = 0;
	public long timeToShoot = 30;

	public void shooting() {
		startShoot++;

		if (startShoot > 0) {
			if (startShoot > timeToShoot) {
				shoot();
				startShoot = 0;
				System.out.println("LA NAVE DISPAROOOOOO++++++++++++");
			}
		} else {
			startShoot = 0;
		}
	}

	public void setPositionFire(PVector pVector) {

		PVector positionForFire = iFrame.coordinatesOf(pVector);
		nave.z = 0;
		nave.y = 0;
		nave.x = (int) positionForFire.x;

	}

	public void invaderShoot(Bullet bullet) {
		if (bullet != null)
			oscFacade.sendMessageInvaderFire((int) bullet.sx, (int) bullet.sy);
	}

	public void myShoot(Bullet bullet) {
		oscFacade.sendMessageYourFire((int) bullet.sx, (int) bullet.sy);
	}

	public void shoot() {
		Bullet bullet = nave.shoot();

		if (bullet != null) {
			myShoot(bullet);
			bulletsList.add(bullet);
		}
	}

}
