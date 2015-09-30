import java.util.*;

public class Star {
	public double x = 0;
	public double y = 0;
	public double dx = 0;
	public double dy = 0;
	public double mass = 0;
	
	public Star(int height,int width){
		Random random = new Random();
		this.x = (random.nextDouble()*width);
		this.y = (random.nextDouble()*height);
		
		//can use random or fixed velocities and masses
		/*this.dx = (random.nextDouble()*0.001)-0.0005;
		this.dy = (random.nextDouble()*0.001)-0.0005;
		this.mass = random.nextDouble()*1000;*/
		this.dx = 0;
		this.dy = 0;
		this.mass = 1000;
	}
	
	//create a large object at the center of the screen
	public Star(int height,int width, double mass){
		Random random = new Random();
		this.x = width/2;
		this.y = height/2;
		
		//can use random or fixed velocities and masses
		//this.dx = (random.nextDouble()*2)-1;
		//this.dy = (random.nextDouble()*2)-1;
		this.dx = 0;
		this.dy = 0;
		this.mass = mass;
	}
	
	//initialize an object in a randomized orbit around a large central mass, with a predetermined maximum orbital distance
	public Star(int height,int width, Gravity sim){
		Random random = new Random();
		double size = 750;
		this.mass = Math.max((1.0-Math.pow(random.nextDouble(),1.0/6))*1000.0,1.0);
		double distance = random.nextDouble()*size;
		double angle = random.nextDouble()*2.0*Math.PI;
		this.x = Math.cos(angle)*distance + sim.stars[0].x;
		this.y = Math.sin(angle)*distance + sim.stars[0].y;
		double speed = Math.sqrt(sim.G * (sim.stars[0].mass+this.mass) / distance) *  (1 + random.nextDouble()*0.5 -0.25);
		this.dx = (sim.stars[0].y-this.y)/distance*speed *  (1 + random.nextDouble()*0.2 -0.1);
		this.dy = (this.x-sim.stars[0].x)/distance*speed *  (1 + random.nextDouble()*0.2 -0.1);
	}
	
	//initialize an object in a randomized orbit around a large central mass, with a user-inputted orbital distance range
	public Star(int height,int width, Gravity sim, double min, double max){
		Random random = new Random();
		double size = max-min;
		this.mass = Math.max((1.0-Math.pow(random.nextDouble(),1.0/6))*10.0,1.0);
		double distance = random.nextDouble()*size + min;
		double angle = random.nextDouble()*2.0*Math.PI;
		this.x = Math.cos(angle)*distance + sim.stars[0].x;
		this.y = Math.sin(angle)*distance + sim.stars[0].y;
		double speed = Math.sqrt(sim.G * (sim.stars[0].mass+this.mass) / distance) *  (1 - random.nextDouble()*0.5 );
		this.dx = (sim.stars[0].y-this.y)/distance*speed *  Math.sqrt(1 + random.nextDouble()*0.2 -0.1);
		this.dy = (this.x-sim.stars[0].x)/distance*speed *  Math.sqrt(1 + random.nextDouble()*0.2 -0.1);
	}
}
