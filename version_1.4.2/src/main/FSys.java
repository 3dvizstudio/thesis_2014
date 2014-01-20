package main;

import processing.core.PGraphics;
import toxi.geom.Circle;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import toxi.processing.ToxiclibsSupport;
import util.Color;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@XmlRootElement(name = "flowgraph")
public class FSys {
	@XmlElement(name = "node")
	protected static ArrayList<Node> nodes = new ArrayList<>();
	@XmlElement(name = "rel")
	protected static ArrayList<Relation> relations = new ArrayList<>();
	@XmlTransient
	private HashMap<Integer, Node> nodeIndex = new HashMap<>();
	@XmlTransient
	private HashMap<Integer, ArrayList<Node>> relationIndex = new HashMap<>();
	@XmlTransient
	private Node activeNode;
	@XmlTransient
	private ArrayList<Node> selectedNodes = new ArrayList<>();
	@XmlTransient
	public static boolean hasActiveNode;

	public void build() {
		for (Node n : nodes) { nodeIndex.put(n.id, n); }
		for (Relation r : relations) {
			ArrayList<Node> nlist = relationIndex.get(r.from);
			if (nlist == null) { nlist = new ArrayList<>(); relationIndex.put(r.from, nlist); }
			nlist.add(nodeIndex.get(r.to));
		} initPhysics();
	}
	private void initPhysics() {
		App.PSYS.getPhysics().clear();
		for (Node c : nodes) {
			App.PSYS.getPhysics().addParticle(c.getVerlet());
			App.PSYS.getPhysics().addBehavior(c.getBehavior());
		} for (Relation r : relations) {
			Node na = getNodeIndex().get(r.from);
			Node nb = getNodeIndex().get(r.to);
			VerletParticle2D va = na.getVerlet();
			VerletParticle2D vb = nb.getVerlet();
			float l = na.getRadius() + nb.getRadius();
			App.PSYS.getPhysics().addSpring(new VerletSpring2D(va, vb, l, 0.01f));
		}
	}
	public void update(App app) {
		if ((App.UPDATE_PHYSVAL) && (App.SCALE != 0)) {
			for (Node n : nodes) n.update();
			for (Relation r : relations) {
				Node na = nodeIndex.get(r.from);
				Node nb = nodeIndex.get(r.to);
				float l = (((na.getRadius() + nb.getRadius()) * App.SCALE) / 2) * App.SPR_SCALE;
				app.getPSYS().getPhysics().getSpring(na.getVerlet(), nb.getVerlet()).setRestLength(l);
			}
		}
	}
	public void clear() { nodes.clear(); relations.clear(); nodeIndex.clear(); relationIndex.clear(); }

	public void draw(ToxiclibsSupport gfx) { drawInfo(gfx); drawNodes(gfx); drawActive(gfx); }

	private void drawInfo(ToxiclibsSupport gfx) {
		PGraphics pg = gfx.getGraphics();
		pg.pushMatrix();
		pg.translate(360, 50);
		pg.fill(0xff666666);
		pg.text("id: name", 0, 0);
		pg.text("col", 50, 0);
		pg.text("size", 100, 0);
		pg.text("rad", 150, 0);
		pg.text("x", 200, 0);
		pg.text("vx", 250, 0);
		pg.text("y", 300, 0);
		pg.text("vy", 350, 0);
		pg.fill(0xff444444);
		for (Node n : nodes) {
			pg.translate(0, 10);
			pg.text(n.id + ": " + n.name, 0, 0);
			pg.text(n.color, 50, 0);
			pg.text((int) n.size, 100, 0);
			pg.text((int) n.radius, 150, 0);
			pg.text((int) n.x, 200, 0);
			pg.text((int) n.verlet.x, 250, 0);
			pg.text((int) n.y, 300, 0);
			pg.text((int) n.verlet.y, 350, 0);
		} pg.noFill();
		pg.popMatrix();
	}

	private void drawNodes(ToxiclibsSupport gfx) { for (Node n : nodes) { n.draw(gfx); } }

	private void drawActive(ToxiclibsSupport gfx) {
		PGraphics pg = gfx.getGraphics();
		if (!selectedNodes.isEmpty()) {
			for (Node n : selectedNodes) { pg.stroke(0xffffff00); pg.ellipse(n.verlet.x, n.verlet.y, 30, 30); pg.noStroke(); }
		}
	}

	public void selectNodeNearPosition(Vec2D mousePos) {
		Circle c = new Circle(mousePos, 20);
		deselectNode();
		for (Node a : nodes) {
			if (c.containsPoint(a.getVerlet())) {
				setActiveNode(a);
				activeNode.getVerlet().lock();
				if (App.isShiftDown) { selectedNodes.add(a); } else {selectedNodes.clear(); selectedNodes.add(a);}
				break;
			}
		} if ((activeNode == null) && (!App.isShiftDown)) { selectedNodes.clear(); }
	}
	public void moveActiveNode(Vec2D mousePos) {
		if (activeNode != null) { activeNode.x = mousePos.x; activeNode.y = mousePos.y; activeNode.verlet.set(mousePos); }
	}
	public void deselectNode() {if (hasActiveNode()) {activeNode.getVerlet().unlock();} activeNode = null;}
	public Node getActiveNode() { return activeNode; }
	private void setActiveNode(Node a) { activeNode = a; activeNode.getVerlet().lock(); }
	public List<Node> getSelectedNodes() { return selectedNodes; }
	public boolean hasActiveNode() { return activeNode != null; }
	public final Node getNodeForID(int id) { return nodeIndex.get(id); }
	public final ArrayList<Node> getRelForID(int id) { return relationIndex.get(id); }
	public HashMap<Integer, Node> getNodeIndex() { return nodeIndex; }
	public void setRelations(ArrayList<Relation> relations) {FSys.relations = relations;}
	public void setNodes(ArrayList<Node> nodes) {FSys.nodes = nodes;}
	public void addNode(Node n) {nodes.add(n);}
	public void addRelation(Relation r) {relations.add(r);}

	@XmlRootElement(name = "node")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Node {

		@XmlAttribute
		private String name;
		@XmlAttribute
		private int id;
		@XmlAttribute
		private float size;
		@XmlAttribute
		private float x;
		@XmlAttribute
		private float y;
		@XmlAttribute
		private int color = 180;
		@XmlTransient
		private float radius = (float) (Math.sqrt(size / Math.PI) * App.NODE_SCALE);
		@XmlTransient
		private VerletParticle2D verlet;
		@XmlTransient
		private AttractionBehavior2D behavior;
		public Node() {
			this.verlet = new VerletParticle2D(new Vec2D(getX(), getY()));
			this.behavior = new AttractionBehavior2D(verlet, radius, -1.2f);
		}
		public Node(String name, int id, float size, float x, float y, int color) {
			this.name = name;
			this.id = id;
			this.size = size;
			this.x = x;
			this.y = y;
			this.color = color;
			this.verlet = new VerletParticle2D(new Vec2D(getX(), getY()));
			this.behavior = new AttractionBehavior2D(verlet, radius, -1.2f);
		}
		public String getName() { return name; }
		public void setName(String name) {this.name = name;}
		public float getSize() { return size; }
		public void setSize(float size) {this.size = size;}
		public int getColor() { return color; }
		public void setColor(int color) {this.color = color;}
		public int getId() {return id;}
		public void setId(int id) {this.id = id;}
		public final String toString() {return Integer.toString(id);}
		public AttractionBehavior2D getBehavior() {return behavior;}
		public void setBehavior(AttractionBehavior2D behavior) { this.behavior = behavior;}
		public VerletParticle2D getVerlet() {return verlet;}
		public void setVerlet(VerletParticle2D verlet) { this.verlet = verlet; }
		public float getRadius() { return radius; }
		public void setRadius(float radius) { this.radius = radius; }
		public float getX() {return x;}
		public void setX(float x) { this.x = x; this.verlet.setX(x); }
		public float getY() {return y;}
		public void setY(float y) { this.y = y; this.verlet.setY(y); }

		public void update() {
			float origRad = (float) (Math.sqrt(size / Math.PI));
			float scaledRad = origRad * App.NODE_SCALE * App.SCALE;
			radius = scaledRad + App.NODE_PAD;
			behavior.setRadius(radius * 2);
			behavior.setStrength(App.NODE_STR);
			verlet.setX(x);
			verlet.setY(y);
		}
		public void draw(ToxiclibsSupport gfx) {
			PGraphics pg = gfx.getGraphics();
			pg.stroke(color, 100, 100);
			pg.ellipse(verlet.x, verlet.y, radius, radius);
			pg.stroke(Color.NODE_F);
			pg.ellipse(verlet.x, verlet.y, radius + 2, radius + 2);
			pg.noStroke();
		}
	}

	@XmlRootElement(name = "rel")
	public static class Relation {
		@XmlAttribute
		public int from;
		@XmlAttribute
		public int to;
		public void setTo(int to) {this.to = to;}
		public void setFrom(int from) {this.from = from;}
	}
}
