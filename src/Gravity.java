import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Gravity extends JPanel{
	private static int height = 1080;
	private static int width = 1920;
	public int objects = 500;
	public Star[] stars = new Star[objects];
	public double G = 6.67384 * Math.pow(10, -11) * Math.pow(10,0);
	public double scale = 1;
	public double speed = 1000000;
	public int interval = 15;
	public boolean play = false;
	public double[] xy = {width/2.0,height/2.0};
	public double totalmass = 0;
	
	//initialize a random arrangement of objects
	public Gravity(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)screenSize.getWidth();
		height = (int)screenSize.getHeight();
		//initialize stars
		for(int i = 0; i < objects; i++){
			stars[i] = new Star(height,width);	
		}
		
		//optional function to create several large "objects"
		//for(int i = 2; i < 12 && i < objects; i++){
		//	stars[i].mass = (Math.random()*9.0+1.0)*10000;	
		//}
		
		//Optional functionality for a player-controlled object
		/*play = true;
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.print("Entered: ");
					if (e.getKeyCode() == KeyEvent.VK_UP){
						stars[1].dy += interval/10000.0;
						System.out.println("UP");
					}
					if (e.getKeyCode() == KeyEvent.VK_DOWN){
						stars[1].dy -= interval/10000.0;
						System.out.println("DOWN");
					}
					if (e.getKeyCode() == KeyEvent.VK_LEFT){
						stars[1].dx -= interval/10000.0;
						System.out.println("LEFT");
					}
					if (e.getKeyCode() == KeyEvent.VK_RIGHT){
						stars[1].dx += interval/10000.0;
						System.out.println("RIGHT");
					}
			}
		});
		setFocusable(true);*/
	}
	
	//create system of objects in orbit around a central mass of user-defined size
	public Gravity(double center){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int)screenSize.getWidth();
		height = (int)screenSize.getHeight();
		stars[0] = new Star(height,width,center);
		
		//calculate total mass of system
		totalmass += stars[0].mass;
		for(int i = 1; i < objects; i++){
			stars[i] = new Star(height,width,this);
			stars[i].mass = stars[i].mass/10;
			totalmass += stars[i].mass;
		}
		
		//Optional functionality for a player-controlled object
		/*play = true;
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
		
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.print("Entered: ");
					if (e.getKeyCode() == KeyEvent.VK_UP){
						stars[1].dy -= interval/100000.0;
						//System.out.println("UP");
					}
					else if (e.getKeyCode() == KeyEvent.VK_DOWN){
						stars[1].dy += interval/100000.0;
						//System.out.println("DOWN");
					}
					else if (e.getKeyCode() == KeyEvent.VK_LEFT){
						stars[1].dx -= interval/100000.0;
						//System.out.println("LEFT");
					}
					else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
						stars[1].dx += interval/100000.0;
						//System.out.println("RIGHT");
					}
			}
		});
		setFocusable(true);*/
	}
	
	public static void main(String [] args) throws InterruptedException {
		JFrame frame = new JFrame("Gravity");
		Gravity galaxy = new Gravity(1000000.0);
		frame.add(galaxy);
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		while(true){
			galaxy.move();
			galaxy.repaint();
			Thread.sleep(galaxy.interval);
		}
	}
	
	public void move(){
		for(int i = 0; i < objects; i++){
			if(stars[i].mass != 0){
				//System.out.println(i);
				for(int j = i+1; j < objects; j++){
					if(stars[j].mass != 0){
						Star[] update = grav(stars[i],stars[j]);
						stars[i] = update[0];
						stars[j] = update[1];
					}
					//create new object in outer  system if current object destroyed
					/*if(stars[j].mass == 0){
						stars[j] = new Star(height,width,this,750,1000);
						stars[j].mass *= 0.1;
						totalmass += stars[j].mass;
					}*/
				}
			}
		}
		
		for(int i = 0; i < objects; i++){
			stars[i].x += stars[i].dx*interval/1000*speed;
			stars[i].y += stars[i].dy*interval/1000*speed;
		}
	}
	
	//less intensive move function that only calculates interactions between objects and a stationary central mass
	public void move(boolean sunonly){
		//calculate gravitational acceleration
		for(int i = 1; i < objects; i++){
			if(stars[i].mass > 0){
					Star[] update = grav(stars[i],stars[0]);
					stars[0] = update[0];
					stars[i] = update[1];
			}
		}
		
		//update positions for new velocities
		for(int i = 1; i < objects; i++){
			stars[i].x += stars[i].dx*interval/1000*speed;
			stars[i].y += stars[i].dy*interval/1000*speed;
		}
	}
	
	//calculate gravitational attraction between on object by another object and returns changed results
	public Star[] grav(Star a, Star b){
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		//check for collisions
		//collision defined as occurring when more object is more than half a radii inside another object
		double d = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2))*scale;
		double ra = Math.pow(Math.abs(a.mass),1.0/3);
		double rb = Math.pow(Math.abs(b.mass),1.0/3);
		//objects not colliding
		if(d > (ra+rb/2) && d > (ra/2+rb)){
			dx = dx/d;
			dy = dy/d;
			double f = G * a.mass * b.mass / Math.pow(d, 2)*interval/1000*speed;
			a.dx = f/Math.abs(a.mass) * dx + a.dx;
			a.dy = f/Math.abs(a.mass) * dy + a.dy;
			b.dx = f/Math.abs(b.mass) * -dx + b.dx;
			b.dy = f/Math.abs(b.mass) * -dy + b.dy;
		}
		//objects colliding
		else{
			double mass = a.mass + b.mass;
			//move to new center of mass
			a.x = a.x*a.mass/mass + b.x*b.mass/mass;
			a.y = a.y*a.mass/mass + b.y*b.mass/mass;
			//conserve momentum
			a.dx = a.dx*a.mass/mass + b.dx*b.mass/mass;
			a.dy = a.dy*a.mass/mass + b.dy*b.mass/mass;
			a.mass = mass;
			b.mass = 0;
		}
		return (new Star[] {a,b});
	}
	
	//find center of mass
	public void centermass(){
		xy[0] = 0;
		xy[1] = 0;
		for(int i = 0; i < objects && totalmass != 0; i++){
			if(stars[i].mass > 0){
				xy[0] += stars[i].mass*stars[i].x/totalmass;
				xy[1] += stars[i].mass*stars[i].y/totalmass;
			}
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		//draw background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		//draw objects
		for(int i = 0; i < objects; i++){
			if(stars[i].mass > 0){
				int r = (int)(Math.pow(Math.abs(stars[i].mass),1.0/3)/scale);
				//experimental gravitational collapse radius
				/*double l = (int)(Math.pow(stars[i].mass,1.0/3)/scale);
				if(l*scale > 1000){
					l = Math.max(2000-l,10);
				}
				int r = (int)Math.max(l,2);*/
				g.fillOval((int)stars[i].x-(int)(r/2-0.5), (int)stars[i].y-(int)(r/2-0.5), r, r);
				//coloured outlines for large masses
				/*if(stars[i].mass >= 500){
					if(stars[i].mass >= 10000000){
						g.setColor(Color.RED);
					}
					else if(stars[i].mass >= 1000000){
						g.setColor(Color.YELLOW);
					}
					else if(stars[i].mass >= 5000){
						g.setColor(Color.ORANGE);
					}
					else if(stars[i].mass >= 1000){
						g.setColor(Color.BLUE);
					}
					else{
						g.setColor(Color.GRAY);
					}
					g.drawOval((int)stars[i].x-(int)(r/2-0.5), (int)stars[i].y-(int)(r/2-0.5), r, r);
					g.setColor(Color.WHITE);
				}*/
				//Draw velocity vectors
				//g.drawLine((int)stars[i].x, (int)stars[i].y, (int)(stars[i].x+stars[i].dx*10000), (int)(stars[i].y+stars[i].dy*10000));
			}
		}
		if(stars[1].mass != 0 && play){
			g.setColor(Color.RED);
			int r = (int)(Math.pow(Math.abs(stars[1].mass),1.0/3)/scale);
			//experimental gravitational collapse radius
			/*double l = (int)(Math.pow(Math.abs(stars[1].mass),1.0/3)/scale);
			if(l*scale > 1000){
				l = Math.max(2000-l,10);
				//l = 10;
			}
			int r = (int)Math.max(l,2);*/
			g.fillOval((int)stars[1].x-(int)(r/2-0.5), (int)stars[1].y-(int)(r/2-0.5), r, r);
			g.drawLine((int)stars[1].x, (int)stars[1].y, (int)(stars[1].x+stars[1].dx*100000), (int)(stars[1].y+stars[1].dy*100000));
		}
	}
}