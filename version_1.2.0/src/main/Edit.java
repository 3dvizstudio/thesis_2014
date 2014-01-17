package main;

import toxi.geom.Circle;
import toxi.geom.Vec2D;

public class Edit {

	private static FSys.Node selectedNode;
	public static final boolean edit_mode = true;
	private final App app;

	public Edit(App app) { this.app = app; }

	public void mousePressed() {
		if (App.PSYS != null) { setActiveNode(App.MOUSE); }
	}

	public void mouseDragged() {
		if (selectedNode != null) selectedNode.getVerlet().set(App.MOUSE);
	}

	public void mouseReleased() {
		if (selectedNode != null) { selectedNode.getVerlet().unlock(); selectedNode = null; }
	}

	public void keyPressed() {
		switch (app.key) {
			case '1':
				App.DRAWMODE = "none";
				break;
			case '2':
				App.DRAWMODE = "verts";
				break;
			case '3':
				App.DRAWMODE = "bezier";
				break;
			case '4':
				App.DRAWMODE = "poly";
				break;
			case '5':
				App.DRAWMODE = "info";
				break;
			case '6':
				App.DRAWMODE = "debug";
				break;
			case 'a':
				addNode();
				break;
			case 'f':
				App.PSYS.addAttractor(App.MOUSE);
				break;
			case 'h':
				App.PSYS.setParticleLock(App.MOUSE);
				break;
			case 'x':
				if (selectedNode != null) selectedNode.setSize(App.OBJ_SIZE);
		}
	}
	private void setActiveNode(Vec2D v) {
		Circle c = new Circle(v, 10);
		selectedNode = null;
		for (FSys.Node p : App.FSYS.nodes) {
			if (c.containsPoint(p.verlet)) {
				selectedNode = p;
				selectedNode.getVerlet().lock();
				break;
			}
		}
	}
	private void drawPropertiesWindow() {
		if (selectedNode != null) {
			System.out.println("this is my property");
		}
	}
	void addNode() {
		createNode(App.OBJ_NAME, App.MOUSE, App.OBJ_SIZE, (int) App.OBJ_COLOR, 10);
	}

	void createNode(String name, Vec2D pos, float size, int color, int id) {
		FSys.Node newNode = new FSys.Node();
		newNode.setId(id);
		newNode.setName(name);
		newNode.setSize(size);
		newNode.setPos(pos);
		newNode.setColor(color); App.FSYS.nodes.add(newNode);
		App.PSYS.addParticle(newNode.getVerlet(), newNode.getBehavior());
		newNode.getVerlet().unlock();
	}

	void addRandom(int cnt) {
		for (int i = 0; i < cnt; i++) {
			Vec2D v = new Vec2D(PSys.BOUNDS.getRandomPoint());
			App.PSYS.addAttractor(v);
		}
	}

	void addPerim(int res) {
		for (int i = 0; i < PSys.BOUNDS.height; i += res) {
			Vec2D vl = new Vec2D(PSys.BOUNDS.getLeft() + 20, i + PSys.BOUNDS.getTop());
			Vec2D vr = new Vec2D(PSys.BOUNDS.getRight() - 20, i + PSys.BOUNDS.getTop());
			App.PSYS.addAttractor(vl);
			App.PSYS.addAttractor(vr);
		}
		for (int j = 0; j < PSys.BOUNDS.width; j += res) {
			Vec2D vt = new Vec2D(j + PSys.BOUNDS.getLeft(), PSys.BOUNDS.getTop() + 20);
			Vec2D vb = new Vec2D(j + PSys.BOUNDS.getLeft(), PSys.BOUNDS.getBottom() - 20);
			App.PSYS.addAttractor(vt);
			App.PSYS.addAttractor(vb);
		}
	}
}
	/*	selectedAttractor = null;
		for (AttractionBehavior2D a : PSys.attractors) {
			if (c.containsPoint(a.getAttractor())) {
				selectedAttractor = a;
				break;
			}
		}*/

//		else if (selectedAttractor != null) {selectedAttractor = null;}
//		if (selectedParticle != null) selectedParticle.set(App.MOUSE);
//		else if (selectedAttractor != null) {selectedAttractor.getAttractor().set(App.MOUSE);}

	/*	void setActiveObject(Vec2D v) {
			Circle c = new Circle(v, 10);
			selectedParticle = null;
			for (VerletParticle2D p : PSys.particles) {
				if (c.containsPoint(p)) {
					selectedParticle = p;
					selectedParticle.lock();
					break;
				}
			}
			selectedAttractor = null;
			for (AttractionBehavior2D a : PSys.attractors) {
				if (c.containsPoint(a.getAttractor())) {
					selectedAttractor = a;
					break;
				}
			}
		}*/