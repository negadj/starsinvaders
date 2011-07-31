import spaceinvaders.MouseGrabbers;


public class OffScreen {

	 static public void main(String args[]) {
		// PApplet.main(new String[] { "--bgcolor=#F0F0F0",
		// "spaceinvaders.MouseGrabbers" });
		try{
			MouseGrabbers.main(args);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
