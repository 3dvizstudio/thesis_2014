package main;

import processing.core.PApplet;
import toxi.color.TColor;
import toxi.geom.Circle;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletMinDistanceSpring2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import toxi.physics2d.behaviors.ParticleBehavior2D;
import toxi.processing.ToxiclibsSupport;

import java.util.ArrayList;
import java.util.List;

public class PSys {
	protected PApplet p5;
	private static final Rect BOUNDS = new Rect(350, 50, 1100, 800);
	private static final Rect INBOX = new Rect(0, 0, 600, 100).translate(500, 400);
	private static VerletPhysics2D physics = new VerletPhysics2D();
	private static List<VerletParticle2D> particles;
	private static List<VerletSpring2D> springs;
	private static List<AttractionBehavior2D> attractors;
	private static List<VerletParticle2D> attractorParticles;
	private static List<VerletMinDistanceSpring2D> minDistSprings;
	private static ArrayList<VerletParticle2D> lockedParticles;
	private static ArrayList<VerletParticle2D> unlockedParticles;
	private static boolean update = true;

	public PSys(PApplet $p5) {
		this.p5 = $p5;
		getPhysics().setDrag(App.DRAG);
		getPhysics().setWorldBounds(getBounds().copy().scale(0.9f));
		setParticles(new ArrayList<VerletParticle2D>());
		setSprings(new ArrayList<VerletSpring2D>());
		setAttractors(new ArrayList<AttractionBehavior2D>());
		setAttractorParticles(new ArrayList<VerletParticle2D>());
	}

	public static void setParticles(List<VerletParticle2D> particles) { PSys.particles = particles; }
	public static void setSprings(List<VerletSpring2D> springs) { PSys.springs = springs; }
	public static void setAttractors(List<AttractionBehavior2D> attractors) { PSys.attractors = attractors; }
	public static void setAttractorParticles(List<VerletParticle2D> attractorParticles) { PSys.attractorParticles = attractorParticles; }
	public static void setPhysics(VerletPhysics2D physics) { PSys.physics = physics; }
	public static void setMinDistSprings(List<VerletMinDistanceSpring2D> minDistSprings) { PSys.minDistSprings = minDistSprings; }
	public static void setLockedParticles(ArrayList<VerletParticle2D> lockedParticles) { PSys.lockedParticles = lockedParticles; }
	public static void setUnlockedParticles(ArrayList<VerletParticle2D> unlockedParticles) { PSys.unlockedParticles = unlockedParticles; }
	public static List<AttractionBehavior2D> getAttractors() { return attractors; }
	public static List<VerletMinDistanceSpring2D> getMinDistSprings() { return minDistSprings; }
	public static ArrayList<VerletParticle2D> getLockedParticles() { return lockedParticles; }
	public static ArrayList<VerletParticle2D> getUnlockedParticles() { return unlockedParticles; }
	public static Rect getBounds() { return BOUNDS; }
	public static Rect getInbox() { return INBOX; }
	public VerletPhysics2D getPhysics() { return physics; }
	public List<VerletParticle2D> getParticles() { return particles; }
	public List<VerletParticle2D> getAttractorParticles() {return attractorParticles;}
	public List<VerletSpring2D> getSprings() {return springs;}

	public void display() {
		if (update) getPhysics().update();
		if (App.UPDATE_PHYSVAL) {
			getPhysics().setDrag(App.DRAG);
			for (AttractionBehavior2D a : getAttractors()) {
				a.setRadius(App.ATTR_RAD);
				a.setStrength(App.ATTR_STR);
			} for (VerletSpring2D s : getPhysics().springs) s.setStrength(App.SPR_STR);
		} ToxiclibsSupport gfx = new ToxiclibsSupport(App.P5);
		gfx.stroke(TColor.RED);
		gfx.rect(getBounds());
		gfx = new ToxiclibsSupport(App.P5);
		gfx.stroke(TColor.BLUE);
		gfx.rect(getBounds());
	}
	public void setParticleLock(Vec2D pos) {
		Circle c = new Circle(pos, 10);
		for (VerletParticle2D p : getPhysics().particles) {
			if (c.containsPoint(p)) {
				p.lock();
				break;
			}
		}
	}
	public void addParticle(VerletParticle2D v, ParticleBehavior2D b) {
		getPhysics().addParticle(v);
		getParticles().add(v);
		getPhysics().addBehavior(b);
	}
	public void addSpring(VerletSpring2D s) {
		getPhysics().addSpring(s);
		getSprings().add(s);
	}
	public void addAttractor(Vec2D pos) {
		VerletParticle2D p = new VerletParticle2D(pos);
		getPhysics().addParticle(p);
		getAttractorParticles().add(p);
	}
	public void addBehavior(VerletParticle2D p) {
		AttractionBehavior2D a = new AttractionBehavior2D(p, 200, 1f);
		getPhysics().addBehavior(a);
		getAttractors().add(a);
	}
	public void addMinDistSprings(ArrayList<VerletParticle2D> particleList) {
		for (int i = 1; i < particleList.size(); i++) {
			VerletParticle2D pi = particleList.get(i);
			for (int j = 0; j < i; j++) {
				VerletParticle2D pj = particleList.get(j);
				VerletMinDistanceSpring2D s = new VerletMinDistanceSpring2D(pi, pj, 10, 0.1f);
				getMinDistSprings().add(s);
				getPhysics().addSpring(s);
			}
		}
	}
}