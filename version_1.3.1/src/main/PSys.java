package main;

import processing.core.PApplet;
import toxi.geom.Circle;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletMinDistanceSpring2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import toxi.physics2d.behaviors.ParticleBehavior2D;

import java.util.ArrayList;
import java.util.List;

public class PSys {
	protected PApplet p5;
	public static final Rect BOUNDS = new Rect(350, 50, 1100, 800);
	public static final Rect INBOX = new Rect(0, 0, 600, 100).translate(500, 400);
	public static VerletPhysics2D physics = new VerletPhysics2D();
	public static List<VerletParticle2D> particles;
	public static List<VerletSpring2D> springs;
	public static List<AttractionBehavior2D> attractors;
	public static List<VerletParticle2D> attractorParticles;
	public static List<VerletMinDistanceSpring2D> minDistSprings;
	public static ArrayList<VerletParticle2D> lockedParticles;
	public static ArrayList<VerletParticle2D> unlockedParticles;

	public PSys(PApplet $p5) {
		this.p5 = $p5;
		physics.setDrag(App.DRAG);
		physics.setWorldBounds(BOUNDS.copy().scale(0.9f));
		particles = new ArrayList<>();
		springs = new ArrayList<>();
		attractors = new ArrayList<>();
		attractorParticles = new ArrayList<>();
	}

	public void display() {
		if (App.UPDATE_PHYSICS) physics.update();
		if (App.UPDATE_PHYSVAL) {
			physics.setDrag(App.DRAG);
			for (AttractionBehavior2D a : attractors) {
				a.setRadius(App.ATTR_RAD);
				a.setStrength(App.ATTR_STR);
			}
			for (VerletSpring2D s : physics.springs) s.setStrength(App.SPR_STR);
		}
	}

	public void addParticle(VerletParticle2D v, ParticleBehavior2D b) {
		physics.addParticle(v);
		particles.add(v);
		physics.addBehavior(b);
	}

	public void addSpring(VerletSpring2D s) {
		physics.addSpring(s);
		springs.add(s);
	}

	public void addBehavior(VerletParticle2D p) {
		AttractionBehavior2D a = new AttractionBehavior2D(p, 200, 1f);
		physics.addBehavior(a);
		attractors.add(a);
	}

	public void addAttractor(Vec2D pos) {
		VerletParticle2D p = new VerletParticle2D(pos);
		physics.addParticle(p);
		attractorParticles.add(p);
	}

	public void addMinDistSprings(ArrayList<VerletParticle2D> particleList) {
		for (int i = 1; i < particleList.size(); i++) {
			VerletParticle2D pi = particleList.get(i);
			for (int j = 0; j < i; j++) {
				VerletParticle2D pj = particleList.get(j);
				VerletMinDistanceSpring2D s = new VerletMinDistanceSpring2D(pi, pj, 10, 0.1f);
				minDistSprings.add(s);
				physics.addSpring(s);
			}
		}
	}

	public void setParticleLock(Vec2D pos) {
		Circle c = new Circle(pos, 10);
		for (VerletParticle2D p : physics.particles) {
			if (c.containsPoint(p)) {
				p.lock();
				break;
			}
		}
	}

	public VerletPhysics2D getPhysics() { return physics; }
	public List<VerletParticle2D> getParticles() { return particles; }
	public List<VerletParticle2D> getAttractorParticles() {return attractorParticles;}
	public List<VerletSpring2D> getSprings() {return springs;}
}
/*





	public void keyPressed() {	}
	public void mouseReleased() {	}
	public void mouseDragged() {	}
*/	/*for (VerletParticle2D p : physics.particles) {
			Circle c = new Circle(p, 10);
			if (c.containsPoint(pos)) {
				p.lock();
				break;
			}
		}*/
/*	public void addAttractor(Vec2D pos) {
		VerletParticle2D p = new VerletParticle2D(pos);
		physics.addParticle(p);
		attractorParticles.add(p);
		AttractionBehavior2D a = new AttractionBehavior2D(p, 200, 1f);
		physics.addBehavior(a);
		attractors.add(a);

	}*/
	/*	*/
/*
	private void displayInfo() {
		p5.fill(PHYS_TXT);
		p5.pushMatrix();
		p5.translate(1600, 50);
		p5.text("Springs: " + physics.springs.size(), 3, 10);
		p5.text("Particles: " + physics.particles.size(), 3, 20);
		p5.text("Drag : " + App.DF3.format(physics.getDrag()), 3, 30);
		p5.text("Separation : " + App.ATTR_RAD, 3, 40);
		p5.text("Spring Str : " + App.DF3.format(App.SPR_STR), 3, 50);
		p5.popMatrix();
		p5.noFill();
	}

	private void drawParticles() {
		p5.fill(PHYS_PTCL);
		for (VerletParticle2D p : physics.particles) {
			if (p == selectedParticle) { p5.ellipse(p.x, p.y, 8, 8); } else { p5.ellipse(p.x, p.y, 4, 4); }
		}
		p5.noFill();
	}

	private void drawSrings() {
		p5.stroke(PHYS_SPR);
		for (VerletSpring2D s : springs) { p5.line(s.a.x, s.a.y, s.b.x, s.b.y); }
		p5.noStroke();
	}

	private void drawMinDistSrings() {
		p5.stroke(PHYS_SPR);
		for (VerletSpring2D s : minDistSprings) { p5.line(s.a.x, s.a.y, s.b.x, s.b.y); }
		p5.noStroke();
	}

	private void drawAttractors() {
		if (attractors != null) {
			p5.stroke(PHYS_ATTR);
			for (AttractionBehavior2D a : attractors) {
				Vec2D n = a.getAttractor();
				if (a == selectedAttractor) { p5.stroke(100); } else { p5.stroke(PHYS_ATTR); }
				p5.line(n.x - 3, n.y - 3, n.x + 3, n.y + 3);
				p5.line(n.x - 3, n.y + 3, n.x + 3, n.y - 3);
				p5.ellipse(n.x, n.y, a.getRadius() * 2, a.getRadius() * 2);
			}
			p5.noStroke();
		}
	}
*/
