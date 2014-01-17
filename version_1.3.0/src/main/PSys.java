package main;

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.Circle;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import toxi.physics2d.behaviors.ParticleBehavior2D;
import toxi.processing.ToxiclibsSupport;
import util.Color;

import java.util.ArrayList;
import java.util.List;

public class PSys {
	protected PApplet p5;
	private final VerletPhysics2D physics;
	private final List<AttractionBehavior2D> attractors;
	private final Rect bounds;
	private AttractionBehavior2D selectedAttractor;
	private Vec2D clickOffset;
	private float separation = 20;
//	private final List<VerletParticle2D> particles;

//	private static List<VerletParticle2D> attractorParticles;
//	private final List<VerletSpring2D> springs;
//	private final Rect INBOX/* = new Rect(350, 50, 150, 800)*/;
//	private static List<VerletMinDistanceSpring2D> minDistSprings;
//	private static ArrayList<VerletParticle2D> lockedParticles;
//	private static ArrayList<VerletParticle2D> unlockedParticles;

	public PSys(int width, int height) {
		/*this.p5 = $p5;*/
		physics = new VerletPhysics2D();
		physics.setDrag(App.DRAG);
		attractors = new ArrayList<>();
		bounds = new Rect(350, 50, 1100, 800);
		physics.setWorldBounds(bounds);
//		springs = new ArrayList<>();
//		particles = new ArrayList<>();
//		attractors=new ArrayList<>();attractorParticles=new ArrayList<>();
	}

	//	public static List<VerletMinDistanceSpring2D> getMinDistSprings() { return minDistSprings; }
//	public static ArrayList<VerletParticle2D> getLockedParticles() { return lockedParticles; }
//	public static ArrayList<VerletParticle2D> getUnlockedParticles() { return unlockedParticles; }
	/*	public Rect getInbox() { return INBOX; }*/
	//	public void setParticles(List<VerletParticle2D> p) { particles = p; }
//	public void setSprings(List<VerletSpring2D> springs) { springs = springs; }
//	public static void setAttractors(List<AttractionBehavior2D> attractors) { attractors = attractors; }
//	public static void setAttractorParticles(List<VerletParticle2D> attractorParticles) { attractorParticles = attractorParticles; }
//	public static void setPhysics(VerletPhysics2D physics) { PSys.physics = physics; }
//	public static void setMinDistSprings(List<VerletMinDistanceSpring2D> minDistSprings) { PSys.minDistSprings = minDistSprings; }
//	public static void setLockedParticles(ArrayList<VerletParticle2D> lockedParticles) { PSys.lockedParticles = lockedParticles; }
//	public static void setUnlockedParticles(ArrayList<VerletParticle2D> unlockedParticles) { PSys.unlockedParticles = unlockedParticles; }
	/*public List<VerletParticle2D> getAttractorParticles() {return attractorParticles;}*/

//	public List<VerletParticle2D> getParticles() { return particles; }
//	public List<VerletSpring2D> getSprings() {return springs;}

	public void update() {
		getPhysics().update();
		getPhysics().setDrag(App.DRAG);
		for (AttractionBehavior2D a : attractors) { a.setRadius(App.ATTR_RAD); a.setStrength(App.ATTR_STR); }
		for (VerletSpring2D s : physics.springs) { s.setStrength(App.SPR_STR); }
		for (VerletParticle2D n : physics.particles) { n.setWeight(2); }
	}
	public void draw(ToxiclibsSupport gfx) {
		PGraphics pg = gfx.getGraphics();
		pg.stroke(0xff222222);
		gfx.rect(getBounds());
		pg.stroke(0xff666666);
		for (VerletParticle2D a : physics.particles) {
			float p_wght = a.getWeight();
			gfx.circle(a, p_wght);
			pg.fill(0xff444444);
			pg.text("v_weight " + p_wght, a.x + p_wght, a.y);
			pg.noFill();
		}
		pg.stroke(0xff333333);
		for (ParticleBehavior2D b : physics.behaviors) {
			AttractionBehavior2D ba = (AttractionBehavior2D) b;
			float b_rad = ba.getRadius();
			gfx.circle(ba.getAttractor(), b_rad);
			pg.fill(0xff444444);
			pg.text("b_rad " + b_rad, ba.getAttractor().x + b_rad, ba.getAttractor().y + 10);
			pg.noFill();
		}
/*		pg.stroke(0xffff2222);
		for (AttractionBehavior2D a : getAttractors()) {
			float a_rad = a.getRadius();
			gfx.circle(a.getAttractor(), a.getRadius());
			pg.fill(0xffff4444);
			pg.text("a_rad " + a_rad, a.getAttractor().x, a.getAttractor().y + 20);
			pg.noFill();
		}*/
		pg.noStroke();
		displayInfo(gfx);
	}
	public void displayInfo(ToxiclibsSupport gfx) {
		PGraphics pg = gfx.getGraphics();
		pg.pushMatrix();
		pg.translate(1200, 50);
		pg.fill(Color.PHYS_TXT);
		pg.text("Springs: " + physics.springs.size(), 0, 0);
		pg.text("Particles: " + physics.particles.size(), 0, 10);
		pg.text("Behaviors: " + physics.behaviors.size(), 0, 20);
		pg.text("Attractors: " + attractors.size(), 0, 30);
		pg.text("Drag : " + App.DF3.format(physics.getDrag()), 0, 40);
		pg.text("Separation : " + separation, 0, 50);
		pg.noFill();
		pg.popMatrix();
	}

	public void addParticle(Vec2D pos, float separation) {
		VerletParticle2D p = new VerletParticle2D(pos);
		physics.addParticle(p);
		physics.addBehavior(new AttractionBehavior2D(p, separation, -1.2f));
	}
	public void addAttractor(Vec2D pos) {
		AttractionBehavior2D a = new AttractionBehavior2D(pos, 200, 1f);
		physics.addBehavior(a);
		attractors.add(a);
	}
	public void selectAttractorNearPosition(Vec2D mousePos) {
		selectedAttractor = null;
		for (AttractionBehavior2D a : attractors) {
			Circle c = new Circle(a.getAttractor(), a.getRadius());
			if (c.containsPoint(mousePos)) { selectAttractor(a); clickOffset = mousePos.sub(c); }
		}
	}
	public void deselectAttractor() { selectedAttractor = null; }
	public void moveSelectedAttractor(Vec2D mousePos) {
		if (selectedAttractor != null) { selectedAttractor.getAttractor().set(mousePos.sub(clickOffset)); }
	}
	private void selectAttractor(AttractionBehavior2D a) { selectedAttractor = a; }

	public void clear() { deselectAttractor(); physics.clear(); attractors.clear(); }
	public void setDrag(float newDrag) { physics.setDrag(newDrag);}
	public void setSeparation(float s) {
		separation = s;
		for (ParticleBehavior2D p : physics.behaviors) {
			if (!attractors.contains(p)) { AttractionBehavior2D a = (AttractionBehavior2D) p; a.setRadius(separation); }
		}
	}

	public boolean hasSelectedAttractor() { return selectedAttractor != null; }
	public VerletPhysics2D getPhysics() { return physics; }
	public float getSeparation() { return separation; }
	public float getDrag() {return physics.getDrag();}
	public Rect getBounds() { return bounds; }
	public List<AttractionBehavior2D> getAttractors() { return attractors; }
	public AttractionBehavior2D getSelectedAttractor() { return selectedAttractor; }
}
/*	public void setParticleLock(Vec2D pos) {
		Circle c = new Circle(pos, 10);
		for (VerletParticle2D p : getPhysics().particles) {
			if (c.containsPoint(p)) {
				if (p.isLocked()) p.unlock();
				else p.lock();
				break;
			}
		}
	}
	*/
/*	public void createSpring(VerletParticle2D a, VerletParticle2D b, float len, float str) {
		VerletSpring2D spring2D = new VerletSpring2D(a, b, len, str);
	}*/
		/*public void addBehavior(VerletParticle2D p) {
		AttractionBehavior2D a = new AttractionBehavior2D(p, 200, 1f);
		physics.addBehavior(a);
		attractors.add(a);
	}*/
	/*	public void addSpring(VerletSpring2D s) {
			getPhysics().addSpring(s);
			getSprings().add(s);
		}*/
	/*	public void addAttractor(Vec2D pos) {
			VerletParticle2D p = new VerletParticle2D(pos);
			physics.addParticle(p);
			attractorParticles.add(p);
		}*/
/*














public void addMinDistSprings(ArrayList<VerletParticle2D> particleList) {
		for (int i = 1; i < attractorParticles.size(); i++) {
			VerletParticle2D pi = attractorParticles.get(i);
			for (int j = 0; j < i; j++) {
				VerletParticle2D pj = attractorParticles.get(j);
				VerletMinDistanceSpring2D s = new VerletMinDistanceSpring2D(pi, pj, 100, 0.1f);
				minDistSprings.add(s);
				physics.addSpring(s);
			}
		}
	}*/