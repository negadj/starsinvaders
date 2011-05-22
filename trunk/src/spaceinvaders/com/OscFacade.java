package spaceinvaders.com;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class OscFacade {

	OscP5 oscP5;
	NetAddress remoteLocation;

	public void setup(PApplet pApplet, String remoteIp, int remotePort) {
		oscP5 = new OscP5(pApplet, 12000);
		remoteLocation = new NetAddress(remoteIp, remotePort);
	}

	public void sendMessageInvaderFire(int x, int y) {

		OscMessage myMessage = new OscMessage("/invaderFire");

		myMessage.add(x); /* add an int to the osc message */
		myMessage.add(y); /* add an int to the osc message */

		oscP5.send(myMessage, remoteLocation);
	}
	
	public void sendMessageYourFire(int x, int y) {

		OscMessage myMessage = new OscMessage("/yourFire");

		myMessage.add(x); /* add an int to the osc message */
		myMessage.add(y); /* add an int to the osc message */

		oscP5.send(myMessage, remoteLocation);
	}
}
